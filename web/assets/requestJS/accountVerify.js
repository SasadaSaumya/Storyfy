const verifyAccount = async () => {

    const dto = {
        verification: document.getElementById("verification").value
    };

    const response = await fetch(
        "UserVerification",
        {
            method: "POST",
            body: JSON.stringify(dto),
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
                message: "Your Account Verification Success"
            });

            window.location = "index.html";

        } else {
            popup.error({
                title: 'Error',
                message: json.content
            });
        }


    } else {
        popup.error({
            title: 'Error',
            message: "Please try again later"
        });

    }



};