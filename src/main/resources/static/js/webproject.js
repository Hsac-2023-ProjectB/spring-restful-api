function pageMove(){
    location.href="editProfile.html"
}

function readImg(input){
    if(input.files && input.files[0]){
        var reader = new FileReader();
        reader.onload = function(e){
            document.getElementById("profile").src=e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
    else{
        document.getElementById("profile").src="";
    }
}