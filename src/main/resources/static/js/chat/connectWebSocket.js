let sock = new SockJS("/ws/chat");  // 웹소켓 연결 요청
let ws = Stomp.over(sock);
let reconnect = 0;

// vue.js
let vm = new Vue({
    el: '#app',
    data: {
        roomId: '',       // 채팅방 고유 id
        room: {},         // roomId로 채팅방 조회해서 가져온 채팅방 정보
        senderName: '',   // 메시지 발신자 이름
        senderId: '',     // 메시지 발신자 id
        message: '',      // 메시지 내용
        messages: [],      // 메시지 객체 저장 배열
        selectedMessageId: null,    // 삭제할 메시지 저장
        selectedMessageIndex: null, // 삭제할 메시지 인덱스 저장
        targetLeft: false,          // 채팅 상대가 떠났는지 판별
    },
    async created() {
        moment.locale('ko');    // 메시지 생성 시각 오전, 오후로 표시

        this.roomId = localStorage.getItem('wschat.roomId');
        this.senderId = localStorage.getItem('wschat.loginMemberId');     // 로그인한 사용자의 id
        this.senderName = localStorage.getItem('wschat.loginMemberName');     // 발신자(로그인한 사용자) 이름
        this.findRoom();

        // 채팅 구독
        this.connectChat = this.connectChat.bind(this);
        this.connectChat();

        // 페이지가 포커스될 때 readMessage 함수 실행
        window.addEventListener('focus', () => {
            setTimeout(() => {
                this.readMessage();
            }, 100);
        });

        // 페이지 변화 감지하여 readMessage 함수 실행
        const observer = new MutationObserver(() => {
            setTimeout(() => {
                this.readMessage();
            }, 100);
        });
        observer.observe(document, {childList: true, subtree: true});
    },
    watch: {
        // 화면 변경 시 스크롤을 최하단으로 옮긴다.
        messages: function() {
            this.scrollToBottom();
        }
    },
    methods: {
        findRoom: function() {
            // roomId로 조회 후 채팅방 정보를 가져온다.
            axios.get('/chat/room/' + this.roomId).then(response => { this.room = response.data; });
        },
        connectChat: function () {
            // pub/sub event
            ws.connect({},
                (frame) => {
                    // 채팅방 구독
                    let chatSubscription = ws.subscribe("/sub/chat/room/" + this.roomId, (message) => {
                        let recv = JSON.parse(message.body);
                        this.recvMessage(recv);
                    });

                    // 메시지를 삭제하거나 읽으면 화면 새로고침
                    let reloadSubscription = ws.subscribe("/sub/chat/reload/" + this.roomId, (message) => {
                        location.reload(); // 페이지 새로고침
                    });

                    // 상대방이 채팅방을 떠난 경우 처리
                    let leftSubscription = ws.subscribe("/sub/chat/targetLeft/" + this.senderId, (message) => {
                        console.log("leave!! target: " + this.senderId);
                        this.targetLeft = true;
                    })

                    // 채팅방 나가면 구독 해제
                    ws.subscribe("/sub/chat/leave/" + this.senderId, (message) => {
                        chatSubscription.unsubscribe();
                        reloadSubscription.unsubscribe();
                        leftSubscription.unsubscribe();
                        window.close(); // 채팅방 닫기
                    })

                    this.getMessages();
                }, (error) => {
                    if (reconnect++ <= 5) {
                        setTimeout(function () {
                            console.log("connection reconnect");
                            sock = new SockJS("/ws/chat");
                            ws = Stomp.over(sock);
                            this.connectChat();
                        }, 10 * 1000);
                    }
                });
        },
        getMessages: function() {
            // roomId로 채팅방 메시지 정보를 가져온다.
            // 채팅방에 메시지가 없으면 response.data가 null이기 때문에 null이 아닐 때만 this.messages에 저장한다.
            console.log("getMessages!");
            axios.get('/chat/messages/' + this.roomId).then(response => {this.messages = response.data ? response.data : [];});
        },
        // 메시지 전송
        sendMessage: function() {
            // 아무것도 입력되지 않았으면 전송하지 않는다.
            // 채팅 상대가 떠났으면 전송하지 않는다.
            if (this.message.trim() === '' || this.targetLeft)
                return;

            ws.send("/pub/chat/message", {}, JSON.stringify({
                messageId: 0,
                roomId: this.roomId,
                senderName: this.senderName,
                senderId: this.senderId,
                message: this.message,
                type: 'TALK',
                checked: 'false'
            }));
            this.message = '';
        },
        // 받은 메시지를 messages 배열에 추가
        recvMessage: function(recv) {
            this.messages.push({
                "messageId":recv.messageId,
                "roomId":recv.roomId,
                "senderName":recv.senderName,
                "senderId":recv.senderId,
                "message":recv.message,
                "type":recv.type,
                "checked": recv.checked,
                "createdAt": moment(recv.createdAt),
            });
        },
        // 나와 상대방 대화 내용에 서로 다른 스타일 적용을 위한 함수
        isMyChat(message) {
            return this.senderId == message.senderId;
        },
        isOtherChat(message) {
            return this.senderId != message.senderId;
        },
        // 새로운 채팅이 발생하면 스크롤을 최하단으로 옮기는 함수
        scrollToBottom: function () {
            this.$nextTick(function () {
                var listGroup = this.$refs.listGroup;
                listGroup.scrollTop = listGroup.scrollHeight;
            });
        },
        readMessage: function() {
            console.log("readMessage!!");
            ws.send("/pub/chat/read", {}, JSON.stringify({
                messageId: 0,
                roomId: this.roomId,
                senderName: this.senderName,
                senderId: this.senderId,
                message: '',
                type: 'ENTER',
                checked: true
            }));
        },
        // 메시지 생성 시각 표시 포멧
        formattedCreatedAt(message, index) {
            // 첫 번째 메시지는 항상 시간을 표시
            // 이전 메시지와 전송자가 다른 경우 시간 표시
            if (index === 0 || message.senderId != this.messages[index - 1].senderId) {
                return this.formatTime(message.createdAt);
            }

            // 이전 메시지의 생성 시간과 현재 메시지의 생성 시간을 비교
            let current = moment(message.createdAt);
            let previous = moment(this.messages[index - 1].createdAt);

            // 분이 바뀌었다면 현재 메시지의 생성 시간을 표시
            if (current.minutes() !== previous.minutes()) {
                return this.formatTime(message.createdAt);
            }

            // 그렇지 않다면 공백 문자열 ('')를 반환
            else {
                return '';
            }
        },
        formatTime(datetime) {
            let m = moment(datetime);
            let hour = m.hour();
            let minute = m.minute();

            // 오전/오후를 결정하고, 12시간제로 변환합니다.
            let period = hour < 12 ? "오전" : "오후";
            if (hour === 0) { hour = 12; }
            else if (hour > 12) { hour -= 12; }

            // 결과 문자열을 반환합니다.
            return `${period} ${hour}:${minute.toString().padStart(2, "0")}`;
        },
        // 삭제 버튼 띄우기 조건 판단
        shouldShowDeleteButton(index) {
            return this.selectedMessageIndex == index && this.selectedMessageId != null;
        },
        // 메시지 삭제 버튼 띄우기
        showDeleteButton(message, index) {
            // 자신의 메시지만 지울 수 있다.
            // 이미 삭제된 메시지는 삭제할 수 없다.
            if (message.senderId == this.senderId && message.type != "DELETED") {
                this.selectedMessageId = message.messageId;
                this.selectedMessageIndex = index;
            }
        },
        deleteMessage() {
            axios.get('/chat/message/' + this.selectedMessageId)
                .then(response => { console.log(response.data.message); })
                .catch(error => console.error(error));

            this.selectedMessageId = null; // 선택 초기화
        },
    }
});