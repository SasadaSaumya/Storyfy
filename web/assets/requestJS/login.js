//Sign Up
const SignUp = async() => {

    const popup = Notification();

    popup.setProperty({
        duration: 5000,
        isHidePrev: true
    });

    const userDataDTO = {
        first_name: document.getElementById("first_name").value,
        last_name: document.getElementById("last_name").value,
        email: document.getElementById("register_email").value,
        password: document.getElementById("register_password").value
    };

    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body: JSON.stringify(userDataDTO),
                header: {
                    "Content-Type": "application/json"
                }
            }

    );

    if (response.ok) {

        const json = await response.json();

        console.log(json);

        if (json.success) {

            popup.success({
                title: 'Success',
                message: "User Sign Up Success"
            });

        } else {
            popup.error({
                title: 'Error',
                message: json.content
            });
        }


    } else {
        console.log("Something went wrong");
    }

};