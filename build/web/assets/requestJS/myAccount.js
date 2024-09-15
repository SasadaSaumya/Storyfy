//load add product nav => product features 

const loadProductFeatures = async () => {

    const response = await fetch("LoadProductFeatures");


    if (response.ok) {
        const json = await response.json();

        console.log(json);

        loadSelectTag("categorySelect", json.categoryList, "name");
        loadSelectTag("author", json.authorList, "name");
        loadSelectTag("publisher", json.publisherList, "name");
        loadSelectTag("status", json.statusList, "name");

        LoadUserData(json.userData);
        loadUserAddress(json.userAddressList);

        loadUserAddedProducts(json.userAddedProductList, "userAdded");


    } else {
        console.log("Something went wrong");

    }


};

const loadSelectTag = (selectTagId, list, property) => {

    const selectTag = document.getElementById(selectTagId);
    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item.id;
        optionTag.innerHTML = item[property];
        selectTag.appendChild(optionTag);
    });

};

// add product 
const addProduct = async () => {


    const categorySelectTag = document.getElementById("categorySelect");
    const authorSelectTag = document.getElementById("author");
    const publisherTag = document.getElementById("publisher");
    const isbnTag = document.getElementById("isbn");
    const pagesTag = document.getElementById("page");
    const statusSelectTag = document.getElementById("status");
    const publishedDateTag = document.getElementById("published_date");
    const titleTag = document.getElementById("title");
    const descriptionTag = document.getElementById("description");
    const qtyTag = document.getElementById("qty");
    const priceTag = document.getElementById("price");
    const image1Tag = document.getElementById("image1");

    const data = new FormData();

    data.append("categoryId", categorySelectTag.value);
    data.append("authorId", authorSelectTag.value);
    data.append("publisherId", publisherTag.value);
    data.append("isbn", isbnTag.value);
    data.append("pages", pagesTag.value);
    data.append("statusId", statusSelectTag.value);
    data.append("publishedDate", publishedDateTag.value);
    data.append("title", titleTag.value);
    data.append("description", descriptionTag.value);
    data.append("qty", qtyTag.value);
    data.append("price", priceTag.value);

    data.append("image1", image1Tag.files[0]);


    const response = await fetch(
        "AddProduct",
        {
            method: "POST",
            body: data

        }
    );

    const popup = Notification();

    popup.setProperty({

        isHidePrev: true

    });



    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {

            image1Tag.value = null;
            categorySelectTag.value = 0;
            authorSelectTag.value = 0;
            publisherTag.value = 0;
            isbnTag.value = "";
            pagesTag.value = "";
            statusSelectTag.value = 0;
            publishedDateTag.value = "";
            descriptionTag.value = "";
            titleTag.value = "";
            priceTag.value = "";
            qtyTag.value = "";

            popup.success({
                title: 'Success',
                message: "Product Added"
            });


        } else {
            popup.error({
                title: 'Error',
                message: json.content
            });

        }


    } else {

        popup.error({
            title: 'Error',
            message: "Something went wrong"
        });

        console.log("Something went wrong");
    }



}


//log out req
const SignOut = async () => {

    const response = await fetch("SignOut");

    if (response.ok) {

    } else {
        console.log("something went wrong");
    }

};

//load user detials 
const LoadUserData = async (userData) => {


    document.getElementById("userAccFname").value = userData.first_name;
    document.getElementById("userAccLname").value = userData.last_name;
    document.getElementById("userAccEmail").value = userData.email;

};

//load user address list 
const loadUserAddress = async (addressList) => {


    let addressBox = document.getElementById("addressMainBox");
    document.getElementById("addressMainContainer").innerHTML = "";


    let adNum = 0;
    addressList.forEach(data => {
        let addressBoxNode = addressBox.cloneNode(true);

        addressBoxNode.querySelector("#adNum").innerHTML = adNum += 1;
        addressBoxNode.querySelector("#addressName").innerHTML = data.first_name + " " + data.last_name;
        addressBoxNode.querySelector("#addressMobile").innerHTML = data.mobile;
        addressBoxNode.querySelector("#addressLine").innerHTML = data.line1 + " " + data.line2;
        addressBoxNode.querySelector("#addressCode").innerHTML = data.postal_code;



        let mainContainer = document.getElementById("addressMainContainer");
        mainContainer.appendChild(addressBoxNode);

    });


};

//loadUserAddedProducts
const loadUserAddedProducts = async (dataList, prefix) => {

    let productHtml = document.getElementById(prefix + "-product");
    document.getElementById(prefix + "-product-main").innerHTML = "";

    dataList.forEach(item => {

        let productClone = productHtml.cloneNode(true);

        productClone.querySelector("#" + prefix + "-product-image").src = "product-images/" + item.id + "/image1.png";

        productClone.querySelector("#" + prefix + "-product-a1").href = "product.html?id=" + item.id;
        productClone.querySelector("#" + prefix + "-product-a2").href = "product.html?id=" + item.id;

        productClone.querySelector("#" + prefix + "-title").innerHTML = item.title;
        productClone.querySelector("#" + prefix + "-author").innerHTML = item.author.name;

        productClone.querySelector("#" + prefix + "-price").innerHTML = new Intl.NumberFormat(
            "en-US",
            {
                minimumFractionDigits: 2
            }
        ).format(item.price);


        const similarProductMain = document.getElementById(prefix + "-product-main");
        similarProductMain.appendChild(productClone);

    });

};
