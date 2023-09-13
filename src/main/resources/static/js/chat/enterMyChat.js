let sock = new SockJS("/ws/chat");  // 웹소켓 연결 요청
let ws = Stomp.over(sock);
let reconnect = 0;

let vm = new Vue({
    el: '#app',
    data: {
        chatrooms : [],                 // 내가 참여 중인 채팅방 정보
        uncheckedMessages : new Map(),  // 채팅방 별 읽지 않은 메시지 수를 담은 데이터
        loginMemberId: null,            // 로그인 사용자 id
        loginMemberName: null,          // 로그인 사용자 이름
        selectedRoom: null,             // 채팅방 이름 변경 시 선택된 채팅방
        deleteRoom: null,              // 삭제된 채팅방 Id 저장
    },
    async created() {
        // 참여 중인 채팅방 정보를 가져온다.
        this.setMyChatRoom();

        // 로그인 정보를 전역변수에 가져온다.
        this.getLoginInfo();

        // 채팅방 별로 읽지 않은 메시지 수를 담은 데이터를 가져온다.
        this.getUnchecked();

        window.addEventListener('beforeunload', function() {
            ws.disconnect();
        });
    },
    methods: {
        // 마이페이지에서 내가 참여중인 채팅방들 검색해서 chatrooms 로 가져온다.
        setMyChatRoom: async function() {
            console.log("setMyChatRoom");
            await axios.get(`/chat/rooms`)
                .then(response => {
                    this.chatrooms = response.data;
                })
        },
        // 채팅 참여자들의 회원 정보 요청해서 가져오는 함수
        // async - await 사용: get 요청으로 정보를 가져오기 전에 채팅방이 생성되는 것을 막기 위해 동기 처리 필요
        getLoginInfo: async function() {
            console.log("getLoginInfo");
            await axios.get('/chat/info/0')
                .then(response => {
                    this.loginMemberId = response.data.loginMember.id;
                    this.loginMemberName = response.data.loginMember.name;
                })
                .then(() => {
                    // 읽지 않은 메시지 수 업데이트를 위한 웹소켓 연결
                    // 이 위치가 아니면 웹소켓 연결이 제대로 안되므로 코드 옮기면 안된다.
                    this.connectChat();
                })
                .catch(error => {
                    console.error('getLoginInfo Error:', error); // 에러가 발생하면 콘솔에 오류 출력
                });
        },
        getUnchecked: async function() {
            console.log("getUnchecked");
            await axios.get('/chat/unchecked')
                .then(response => {
                    this.uncheckedMessages = new Map(Object.entries(response.data));
                })
                .catch(error => {
                    console.error('getUnchecked Error:', error);
                });
        },
        connectChat: function() {
            console.log("connectChat");
            // pub/sub event
            ws.connect({},
                (frame) => {
                    // 상대가 메시지를 보내면 알림용 메시지를 여기로 보낸다.
                    // 메시지가 도착하면 안 읽은 메시지 수를 다시 받아온다.
                    ws.subscribe("/sub/notification/" + this.loginMemberId, (message) => {
                        this.getUnchecked();
                    });

                    // 내가 메시지를 읽으면 알림용 메시지를 여기로 보낸다.
                    // 읽지 않은 메시지를 다시 받아온다.
                    ws.subscribe("/sub/checked/" + this.loginMemberId, (message) => {
                        this.getUnchecked();
                    });

                    // 새 채팅방이 생성되면 화면 리스트 업데이트
                    ws.subscribe("/sub/newChatRoom/" + this.loginMemberId, (message) => {
                        this.setMyChatRoom();
                        location.reload();
                    });
                }, (error) => {
                    console.log("error");
                    if(reconnect++ <= 5) {
                        setTimeout(function() {
                            console.log("connection reconnect");
                            sock = new SockJS("/ws/chat");
                            ws = Stomp.over(sock);
                            this.connectChat();
                        },10*1000);
                    }
                });
        },
        // 채팅방 입장
        enterRoom: function(item) {
            console.log("enterRoom!");

            // 채팅방에 필요한 정보 넘겨주기
            localStorage.setItem('wschat.roomId', item.roomId);
            localStorage.setItem('wschat.loginMemberId', this.loginMemberId);
            localStorage.setItem('wschat.loginMemberName', this.loginMemberName);

            // 채팅방을 팝업창으로 열기
            let url = "/chat/room/enter";
            let name = "CHATROOM_" + item.roomId + "_" + this.loginMemberId;
            let option = "width = 500, height = 800, top = 100, left = 200, location = no"
            window.open(url, name, option);
        },
        // 이름 변경할 채팅방 정보 저장
        selectRoom: function(room) {
            this.selectedRoom = room;
        },
        // 채팅방 이름 변경 요청
        submitNewRoomName: function() {
            const newRoomName = document.getElementById('newRoomNameInput').value;
            let params = new URLSearchParams();

            params.append("roomId", this.selectedRoom.roomId);
            params.append("newRoomName", newRoomName);

            // Axios 요청 보내기
            axios.post('/chat/roomName', params)
                .then(response => {
                    location.reload(); // 페이지 새로고침
                    this.selectedRoom = null;
                })
                .catch(error => {
                    console.error('Change Room Name Error', error);
                });
        },
        // 채팅방 나가기 기능
        // 채팅방 삭제 요청
        leaveRoom: async function(item) {
            console.log("leave Room!!");
            this.deleteRoom = item.roomId;

            // 채팅방 삭제 후 '상대방이 채팅방을 떠났습니다.' 메시지가 보내면 안돼서 순서대로 처리 필요하다.
            await this.leaveChat();

            axios.get('/chat/leaveRoom/' + item.roomId)
                .then(response => {
                    location.reload(); // 페이지 새로고침
                })
                .catch(error => {
                    console.error('Delete Room Error:', error);
                });

            this.deleteRoom = null;
        },
        // 채팅방 삭제 후처리
        // 삭제한 사람: 채팅방 구독을 중단, 채팅방 창을 종료
        // 채팅 상대방: 상대가 채팅방에서 떠났음을 표시
        leaveChat: function() {
            console.log("leaveChat!!");
            ws.send("/pub/chat/leave", {}, JSON.stringify({
                messageId: 0,
                roomId: this.deleteRoom,
                senderName: this.loginMemberName,
                senderId: this.loginMemberId,
                message: '상대방이 채팅방을 떠났습니다.',
                type: 'LEAVE',
                checked: true
            }));
        },
    }
});