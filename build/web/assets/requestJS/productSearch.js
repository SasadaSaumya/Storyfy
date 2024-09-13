var stProduct = document.getElementById("product-card");

var currentPage = 0;
var paginationButton = document.getElementById("pagination-item");

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

        //load product
        updateProductCard(json);



    } else {

        popup.error({
            title: 'Error',
            message: "Something went wrong"
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


        main.appendChild(sub_clone);
    });


};

const updateProductCard = (json) => {

    //load product


    let stProductContainer = document.getElementById("product-container");

    stProductContainer.innerHTML = "";

    json.productList.forEach(product => {

        let stProductClone = stProduct.cloneNode(true);
        stProductClone.querySelector("#product-image").src = "product-images/" + product.id + "/image1.png";
        stProductClone.querySelector("#product-title-1").innerHTML = product.title;

        stProductClone.querySelector("#product-image-href").href = "product.html?id=" + product.id;

        stProductClone.querySelector("#product-price").innerHTML = new Intl.NumberFormat(
            "en-US",
            {
                minimumFractionDigits: 2
            }
        ).format(product.price);


        stProductContainer.appendChild(stProductClone);
    });


    //pagination
    let paginationContainer = document.getElementById("pagination-container");

    paginationContainer.innerHTML = " ";

    let productCount = json.allProductCount;
    const prePageProductCount = 6;

    let pages = Math.ceil(productCount / prePageProductCount);

    if (currentPage != 0) {
        //Add preview button
        let stpaginationButtonClonePrev = paginationButton.cloneNode(true);
        stpaginationButtonClonePrev.innerHTML = "Prev";
        stpaginationButtonClonePrev.addEventListener("click", e => {
            currentPage--;
            searchProduct(currentPage * 6);

        });
        paginationContainer.appendChild(stpaginationButtonClonePrev);
    }

    for (let i = 0; i < pages; i++) {
        let stpaginationButtonClone = paginationButton.cloneNode(true);
        stpaginationButtonClone.innerHTML = i + 1;

        stpaginationButtonClone.addEventListener("click", e => {
            currentPage = i;
            searchProduct(i * 6);

        });

        if (i == currentPage) {
            stpaginationButtonClone.className = "axil-btn btn-bg-secondary ml--10";
        } else {
            stpaginationButtonClone.className = "axil-btn btn-bg-primary ml--10";

        }

        paginationContainer.appendChild(stpaginationButtonClone);

    }

    if (currentPage != (pages - 1)) {
        //ADD NEXT BTUTTON
        let stpaginationButtonCloneNext = paginationButton.cloneNode(true);
        stpaginationButtonCloneNext.innerHTML = "Next";
        stpaginationButtonCloneNext.addEventListener("click", e => {
            currentPage++;
            searchProduct(currentPage * 6);

        });
        paginationContainer.appendChild(stpaginationButtonCloneNext);
    }

};

const searchProduct = async (firstResult) => {

    //notification 
    const popup = Notification();

    // let categoryName = document.getElementById("radio-cate").checked.innerHTML;

    // let publisherName = document.getElementById("radio-publisher").checked.innerHTML;

    // let authorName = document.getElementById("radio-authors").checked.innerHTML;

    function getCheckedRadioText(inputName) {
        // Select all radio buttons with the given name
        const radios = document.querySelectorAll('input[name="' + inputName + '"]');

        // Iterate through each radio button
        for (const radio of radios) {
            // Check if the radio button is checked
            if (radio.checked) {
                // Get the parent <div> container
                const container = radio.closest('.col-12');
                // Find the <a> tag within that container
                const link = container.querySelector('a');
                // Return the text content of the <a> tag
                return link ? link.textContent : '';
            }
        }
        return ''; // Return an empty string if no radio button is checked
    }

    // Example usage
    let categoryName = getCheckedRadioText("radio-category");
    console.log(categoryName);

    let authorName = getCheckedRadioText("radio-authors");
    console.log(authorName);

    let publisherName = getCheckedRadioText("radio-publisher");
    console.log(publisherName);



    let priceRangeStart = document.getElementById("minValue").innerHTML;
    let priceRangeEnd = document.getElementById("maxValue").innerHTML;

    let sortText = document.getElementById("sortby").value;


    const data = {
        firstResult: firstResult,
        categoryName: categoryName,
        publisherName: publisherName,
        authorName: authorName,
        priceRangeStart: priceRangeStart,
        priceRangeEnd: priceRangeEnd,
        sortText: sortText
    };

    console.log(data);


    // send post request 
    const response = await fetch(
        "SearchProducts",
        {
            method: "POST",
            body: JSON.stringify(data),
            header: {
                "Content-Type": "application/json"
            }
        }
    );

    // request handling
    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {
            console.log("success");

            updateProductCard(json);

        } else {

            popup.error({
                title: 'Error',
                message: "Error"
            });

        }
    } else {
        popup.error({
            title: 'Error',
            message: "Error"
        });
    }

};