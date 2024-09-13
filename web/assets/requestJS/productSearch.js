const reset = () => {
    window.location.reload();
};

const loadProductFeatures = async () => {

    const popup = Notification();

    const response = await fetch("LoadAllProducts");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        //load category list
//        loadOption("category", json.categoryList, "name");
//
//        loadOption("condition", json.conditionList, "name");
//
//        loadOption("color", json.colorList, "name");
//
//        loadOption("storage", json.storageList, "value");
//
//        //load product
//        updateProductView(json);


    } else {

        popup.error({
            title: 'Error',
            message: "Error"
        });

    }
};

