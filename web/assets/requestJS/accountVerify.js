const verifyAccount = async () => {

    const popup = Notification();

    popup.setProperty({
        duration: 5000,
        isHidePrev: true
    });

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


            setTimeout(() => {
                window.location = "index.html";

            }, 2000);


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