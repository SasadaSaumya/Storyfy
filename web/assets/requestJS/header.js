IsSignIn();

const LoadUserHeaderFeatures = async () => {

    const response = await fetch("LoadUserHeader");


    if (response.ok) {
        const json = await response.json();

        console.log(json);


    } else {
        console.log("Something went wrong");

    }


};

async function IsSignIn() {

    const response = await fetch("IsSignIn");

    if (response.ok) {

        const json = await response.json();

        const response_DTO = json.response_DTO;

        if (response_DTO.success) {

            //sign in
            const user = response_DTO.content;
            console.log(user);
            let signinText = document.getElementById("signinText");

            signinText.innerHTML = user.first_name + " " + user.last_name;
            signinText.href = "account.html";

        } else {

            //not signed in

        }



    }

}