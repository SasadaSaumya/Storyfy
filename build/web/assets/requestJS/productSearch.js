// JavaScript code to load products and handle pagination

var stProduct = document.getElementById("product-card");
var currentPage = 0;
var paginationButton = document.getElementById("pagination-item");

const reset = () => {
    window.location.reload();
};

const loadProductFeatures = async () => {
    const popup = Notification();

    try {
        const response = await fetch("LoadAllProducts");
        if (response.ok) {
            const json = await response.json();
            console.log(json);

            // Load category, author, and publisher options
            loadOption("category", json.categoryList);
            loadOption("authors", json.authorList);
            loadOption("publisher", json.publisherList);

            // Load products
            updateProductCard(json);
        } else {
            popup.error({
                title: 'Error',
                message: "Something went wrong"
            });
        }
    } catch (error) {
        popup.error({
            title: 'Error',
            message: "Network error"
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
    let stProductContainer = document.getElementById("product-container");
    stProductContainer.innerHTML = "";

    json.productList.forEach(product => {
        let stProductClone = stProduct.cloneNode(true);
        stProductClone.querySelector("#product-image").src = "product-images/" + product.id + "/image1.png";
        stProductClone.querySelector("#product-title-1").innerHTML = product.title;
        stProductClone.querySelector("#product-image-href").href = "product.html?id=" + product.id;
        stProductClone.querySelector("#product-price").innerHTML = new Intl.NumberFormat("en-US", {
            minimumFractionDigits: 2
        }).format(product.price);

        stProductContainer.appendChild(stProductClone);
    });

    // Pagination
    let paginationContainer = document.getElementById("pagination-container");
    paginationContainer.innerHTML = "";

    let productCount = json.allProductCount;
    const prePageProductCount = 6;
    let pages = Math.ceil(productCount / prePageProductCount);

    if (currentPage != 0) {
        let stpaginationButtonClonePrev = paginationButton.cloneNode(true);
        stpaginationButtonClonePrev.innerHTML = "Prev";
        stpaginationButtonClonePrev.addEventListener("click", () => {
            currentPage--;
            searchProduct(currentPage * prePageProductCount);
        });
        paginationContainer.appendChild(stpaginationButtonClonePrev);
    }

    for (let i = 0; i < pages; i++) {
        let stpaginationButtonClone = paginationButton.cloneNode(true);
        stpaginationButtonClone.innerHTML = i + 1;
        stpaginationButtonClone.addEventListener("click", () => {
            currentPage = i;
            searchProduct(i * prePageProductCount);
        });

        stpaginationButtonClone.className = i === currentPage ? "axil-btn btn-bg-secondary ml--10" : "axil-btn btn-bg-primary ml--10";
        paginationContainer.appendChild(stpaginationButtonClone);
    }

    if (currentPage != (pages - 1)) {
        let stpaginationButtonCloneNext = paginationButton.cloneNode(true);
        stpaginationButtonCloneNext.innerHTML = "Next";
        stpaginationButtonCloneNext.addEventListener("click", () => {
            currentPage++;
            searchProduct(currentPage * prePageProductCount);
        });
        paginationContainer.appendChild(stpaginationButtonCloneNext);
    }
};

const searchProduct = async (firstResult) => {
    const popup = Notification();

    function getCheckedRadioText(inputName) {
        const radios = document.querySelectorAll(`input[name="${inputName}"]`);
        for (const radio of radios) {
            if (radio.checked) {
                const container = radio.closest('.col-12');
                const link = container ? container.querySelector('a') : null;
                return link ? link.textContent : '';
            }
        }
        return '';
    }

    let categoryName = getCheckedRadioText("radio-category");
    let authorName = getCheckedRadioText("radio-authors");
    let publisherName = getCheckedRadioText("radio-publisher");

    let priceRangeStart = parseFloat(document.getElementById("minValue").innerHTML) || 0;
    let priceRangeEnd = parseFloat(document.getElementById("maxValue").innerHTML) || Number.MAX_VALUE;
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

    try {
        const response = await fetch("SearchProducts", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            const json = await response.json();
            if (json.success) {
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
    } catch (error) {
        popup.error({
            title: 'Error',
            message: "Network error"
        });
    }
};
