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

