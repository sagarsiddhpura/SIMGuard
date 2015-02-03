$(document).ready(function(){
	$('.carousal .bxslider').bxSlider({
	  auto: true
	});	
			if($(window).width() < 480){
		 $('nav .navbar').addClass("collapse")}
})

function validate(){
	var userName = $('#userName').val();
	var password = $('#pswd').val();
	if(userName == "" || password == ""){
		$('#error').text('Please enter your login details');
		}
	else{
		loginVal(userName,password);
		}
	}
