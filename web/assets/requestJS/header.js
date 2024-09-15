IsSignIn();

const LoadUserHeaderFeatures = async () => {

    const response = await fetch("LoadUserHeader");


    if (response.ok) {
        const json = await response.json();

        console.log(json);

        loadOptionHeader("category", json.categoryList);
        loadOptionHeader("authors", json.authorList);


    } else {
        console.log("Something went wrong");

    }


};

const loadOptionHeader = (prefix, dataList) => {

    let main = document.getElementById(prefix + "-main-hd");
    let sub = document.getElementById(prefix + "-sub-hd");

    main.innerHTML = "";

    dataList.forEach(data => {
        let sub_clone = sub.cloneNode(true);

        sub_clone.querySelector("#" + prefix + "-text-hd").innerHTML = data.name;


        main.appendChild(sub_clone);
    });


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

