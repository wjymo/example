var app={
	baseUrl:"http://127.0.0.1:8081",
	imageLogin:function(username,password,imageCode,imageCodeKey){
		$.ajax({
			type:"post",
			dataType: "json",//预期服务器返回的数据类型
			url:app.baseUrl+"/user/login?username="+username+"&password="+password+"&imageCode="+imageCode+"&imageCodeKey="+imageCodeKey,
			success: function (result) {
				if(result.code==0){
					console.log("登录成功");
					console.log(JSON.stringify(result.result))
					localStorage.setItem("token",result.result.token);
					localStorage.setItem("id",result.result.id);
					localStorage.setItem("img",result.result.img);
					
					localStorage.setItem("username",result.result.username);

					alert('登录成功');
				}else{
					console.log("登录失败!");
				}
			}
		})
	},
	initImage:function(){
		$.ajax({
			type:"get",
			url:app.baseUrl+"/code/image2",
			success:function(result, textStatus, request){
				var imageCodeKey = request.getResponseHeader("imageCodeKey");
				var contentType = request.getResponseHeader("content-type");
				var responseHeaders=request.getAllResponseHeaders();
				console.log(imageCodeKey)
				$('#image').attr("src",result);
				localStorage.setItem("imageCodeKey",imageCodeKey);
			}
		})
	},
	sendSmsCode:function(){
		$.ajax({
			type:"get",
			url:app.baseUrl+"/code/sms",
			success:function(result, textStatus, request){
				var smsCodeKey = request.getResponseHeader("smsCodeKey");
				console.log(smsCodeKey);
				localStorage.setItem("smsCodeKey",smsCodeKey);
				alert("已发送,code为:"+result)
			}
		})
	},
	smsLogin:function(mobile,smsCode,smsCodeKey){
		$.ajax({
			type:"post",
			dataType: "json",//预期服务器返回的数据类型
			url:app.baseUrl+"/sms/login?mobile="+mobile+"&smsCode="+smsCode+"&smsCodeKey="+smsCodeKey,
			success: function (result) {
				if(result.code==0){
					console.log("登录成功");
					localStorage.setItem("token",result.result.token);
				}else{
					console.log("登录失败!");
				}
			}
		})
	},
	getToken:function(){
		return localStorage.getItem('token');
	},
	getId:function(){
		return localStorage.getItem('id');
	},
	getImg:function(){
		return localStorage.getItem('img');
	},
	getUsername:function(){
		return localStorage.getItem('username');
	},
	test:function(){
		$.ajax({
			type:"get",
			url:app.baseUrl+"/code/test",
			success:function(result){
				alert(result);
			}
		})
	},
	// 转为unicode 编码  
	encodeUnicode:function (str) {  
	    var res = [];  
	    for ( var i=0; i<str.length; i++ ) {  
	    res[i] = ( "00" + str.charCodeAt(i).toString(16) ).slice(-4);  
	    }  
	    return "\\u" + res.join("\\u");  
	} ,
	  
	// 解码  
	decodeUnicode:function (str) {  
	    str = str.replace(/\\/g, "%");  
	    return unescape(str);  
	},
	nettyServerUrl:"ws://192.168.0.110:8088/ws",
	WS_CHAT:{
		socket:null,
		init:function(){
			if(window.WebSocket){
				if (app.WS_CHAT.socket != null
					&& app.WS_CHAT.socket != undefined
					&& app.WS_CHAT.socket.readyState == WebSocket.OPEN) {
					return false;
				}
				app.WS_CHAT.socket=new WebSocket(app.nettyServerUrl);
				app.WS_CHAT.socket.onopen=function(){
					console.log("连接建立成功");
					// 构建ChatMsg
					var chatMsg = new app.ChatMsg(app.getId(), null, null, null);
					// 构建DataContent
					var dataContent = new app.DataContent(app.CONNECT, chatMsg, null);
					// 发送websocket
					app.WS_CHAT.chat(JSON.stringify(dataContent));
				}
				app.WS_CHAT.socket.onclose=function(){
					console.log("连接关闭...");
				}
				app.WS_CHAT.socket.onerror=function(){
					console.log("发生错误...");
				}
				app.WS_CHAT.socket.onmessage=function(e){
					var data=e.data;
					console.log("接收到消息："+data);
					// 转换DataContent为对象
					var dataContent = JSON.parse(e.data);
					var action = dataContent.action;
					var chatMsg=dataContent.chatMsg;
					var msg = chatMsg.msg;
					var friendUserId = chatMsg.senderId;
					var myId = chatMsg.receiverId;
					var currentId = app.getId();
					if(myId == currentId){
						app.receiveMsg(msg);
					}else {
						alert("当前用户id和服务端对应的用户id不符！！！")
					}
					// 接受到消息之后，对消息记录进行签收
					var dataContentSign = new app.DataContent(app.SIGNED, null, chatMsg.msgId);
					app.WS_CHAT.chat(JSON.stringify(dataContentSign));
				}
			}else{
				alert("浏览器不支持webSocket！");
			}
		},
		chat:function(msg){
			if (app.WS_CHAT.socket != null
				&& app.WS_CHAT.socket != undefined
				&& app.WS_CHAT.socket.readyState == WebSocket.OPEN) {
				app.WS_CHAT.socket.send(msg);
			}else {
				// 重连websocket
				app.WS_CHAT.init();
				setTimeout("app.WS_CHAT.reChat('" + msg + "')", "1000");
			}
		}
	},
	reChat: function(msg) {
		console.log("消息重新发送...");
		CHAT.socket.send(msg);
	},
	receiveMsg:function (msg){

		var html='<div id="receive_msg" class="receive_msg_class">'+
			'<div class="receive_msg_content msg_content">'+msg+'</div>'+
			'</div>';
		$('#div_clear').before(html);
	},
	/**
	 * 和后端的枚举对应
	 */
	CONNECT: 1, 	// 第一次(或重连)初始化连接
	CHAT: 2, 		// 聊天消息
	SIGNED: 3, 		// 消息签收
	KEEPALIVE: 4, 	// 客户端保持心跳
	PULL_FRIEND:5,	// 重新拉取好友
	
	/**
	 * 和后端的 ChatMsg 聊天模型对象保持一致
	 * @param {Object} senderId
	 * @param {Object} receiverId
	 * @param {Object} msg
	 * @param {Object} msgId
	 */
	ChatMsg: function(senderId, receiverId, msg, msgId){
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.msg = msg;
		this.msgId = msgId;
	},
	
	/**
	 * 构建消息 DataContent 模型对象
	 * @param {Object} action
	 * @param {Object} chatMsg
	 * @param {Object} extand
	 */
	DataContent: function(action, chatMsg, extand){
		this.action = action;
		this.chatMsg = chatMsg;
		this.extand = extand;
	}

}