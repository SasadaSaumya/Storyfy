const reset = () => {
    window.location.reload();
};

const loadProductFeatures = async () => {

    const popup = Notification();

    const response = await fetch("LoadAllProducts");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        // load category list

        loadOption("category", json.categoryList);

        loadOption("authors", json.authorList);

        loadOption("publisher", json.publisherList);
        // //
        //        //load product
        //        updateProductView(json);



    } else {

        popup.error({
            title: 'Error',
            message: "Error"
        });

    }
};


const loadOption = (prefix, dataList) => {

    let main = document.getElementById(prefix + "-main");
    let sub = document.getElementById(prefix + "-sub");

    main.innerHTML = "";

    dataList.forEach(data => {
        let sub_clone = sub.cloneNode(true);

        sub_clone.querySelector("#" + prefix + "-text").innerHTML = data.name;
        console.log(data.name);

        main.appendChild(sub_clone);
    });


};
