let targetId;                   // 페이지 작성자 id
let targetName;                 // 페이지 작성자 이름
let loginMemberId;              // 로그인 사용자 id
let loginMemberName;            // 로그인 사용자 이름
const profileId = window.profileId;   // 채팅 상대방 id = 페이지 id

let vm = new Vue({
    el: '#app',
    data: {
        roomId : 'roomId',              // 채팅방 id
        roomName : 'roomName',          // [상대방이름] 님과의 채팅방
        subMembers : 'subMembers',      // 구독자: 채팅 요청자 - 수신자 id
    },
    methods: {
        // 채팅 참여자들의 회원 정보 요청해서 가져오는 함수
        // async - await 사용: get 요청으로 정보를 가져오기 전에 채팅방이 생성되는 것을 막기 위해 동기 처리 필요
        getLoginInfo: async function() {
            await axios.get('/chat/info/' + profileId)
                .then(response => {
                    loginMemberId = response.data.loginMember.id;
                    loginMemberName = response.data.loginMember.name;
                    targetId = response.data.targetMember.id;
                    targetName = response.data.targetMember.name;
                }).catch(error => {
                    console.error('getLoginInfo Error:', error); // 에러가 발생하면 콘솔에 오류 출력
                });
        },
        // 기존 채팅방이 존재하는지 검사
        searchRoom: async function() {
            console.log("searchRoom!");

            // 채팅 참여자들의 정보를 전역변수에 가져온다.
            await this.getLoginInfo();

            // 채팅방 id 생성 시 작은숫자가 앞에 오도록 한다.
            if(loginMemberId < targetId) {
                this.roomId = loginMemberId + "-" + targetId;
            }
            else this.roomId = targetId + "-" + loginMemberId;

            this.roomName = loginMemberName + ", " + targetName;
            this.subMembers = loginMemberId + "-" + targetId;

            axios.get(`/chat/room/` + this.roomId)
                .then(response => {  // 로그인 id 정보로 조회해서 가져온다.

                    if(response.data.roomId != null) { // 기존 채팅방이 존재하면 입장
                        this.enterRoom();
                    }
                    else { // 기존 채팅방이 없으면 새로 만들고 입장
                        this.createRoom();
                    }
                })
                .catch(error => {
                    console.error('searchRoom Error:', error); // 에러가 발생하면 콘솔에 오류 출력
                });
        },
        // 기존 채팅방이 없으면 생성
        createRoom: function() {
            console.log("createRoom!");
            let params = new URLSearchParams();

            // 방 생성용 정보들을 post 하면서 같이 보낸다.
            params.append("roomId", this.roomId);
            params.append("roomName",this.roomName);
            params.append("subMembers", this.subMembers);
            params.append("creatorId", loginMemberId);

            axios.post('/chat/room', params)
                .then(
                    response => {
                        this.enterRoom(null);
                    }
                )
                .catch( response => { alert("채팅방 개설에 실패하였습니다."); } );
        },
        // 채팅방 입장
        enterRoom: function() {
            console.log("enterRoom!");

            // 채팅방에 필요한 정보 넘겨주기
            localStorage.setItem('wschat.roomId', this.roomId);
            localStorage.setItem('wschat.loginMemberId', loginMemberId);
            localStorage.setItem('wschat.loginMemberName', loginMemberName);

            console.log("startChat.js TEST - roomId: " + this.roomId);

            // 채팅방을 팝업창으로 열기
            let url = "/chat/room/enter";
            let name = "CHATROOM_" + this.roomId + "_" + loginMemberId;
            let option = "width = 500, height = 800, top = 100, left = 200, location = no"
            window.open(url, name, option);
        },
    }
});