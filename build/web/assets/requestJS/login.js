//Sign Up
const SignUp = async() => {

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

    } else {
        console.log("Something went wrong");
    }

};