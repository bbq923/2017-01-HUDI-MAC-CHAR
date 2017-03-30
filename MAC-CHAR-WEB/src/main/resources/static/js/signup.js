let signup = (function(){
	let passwordInputs = document.querySelectorAll('input[type="password"]');
	let button = document.querySelector("#signup_form > button");
	
	function validation() {
		let password = document.querySelector("#signup_form > div:nth-child(2) > input").value;
		let passwordConfirm = document.querySelector("#signup_form > div:nth-child(3) > input").value;
		if (password === passwordConfirm) {
			button.disabled = false;
			button.style.opacity = "1";
			button.style.cursor = "pointer";
		} else {
			button.disabled = true;
			button.style.opacity = ".3";
			button.style.cursor = "default";
		}
	}
	
	function trySignup(e) {
		e.preventDefault();
		
		let url = document.querySelector('#signup_form').getAttribute("action");
		let userInputs = document.querySelectorAll('[name]');
		let jsonString = JSON.stringify({"email" : userInputs[0].value,
										 "password" : userInputs[1].value,
										 "nickname" : userInputs[2].value});
		
		$.ajax({
			type: 'post',
			url: url,
			data : jsonString,
			contentType: 'application/json',
			dataType: 'json',
			error: onError,
			success: onSuccess
		});
	}

	function onSuccess(data, status) {
		console.log(data);
		
		if (data.statusCode === "Ok") {
			location.href="/login";
		}
		
		if (data.statusCode === "EmailExists") {
			userNotify(data.errorMessage);
		}
	}
	
	function onError(xhr, status) {
		alert("error");
	}
	
	// 회원 가입 실패시 사용자에게 원인을 알려준다.
	function userNotify(errorMessage) {
		console.log("noty called?");
		let inputForm = document.querySelector('.input_form');
		let notify = document.createElement('p');
		notify.innerHTML = errorMessage;
		notify.style.color = "red";
		inputForm.style.paddingBottom = "0";
		inputForm.appendChild(notify);
	}
	
	function init() {
		for (let inputBox of passwordInputs) {
			inputBox.addEventListener("keyup", validation);
		}
		button.addEventListener("click", trySignup);
	}
	
	return {
		init: init
	}
})(); // self executing function

(function(){
	signup.init();
})();


