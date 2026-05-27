const chatbotToggle = document.getElementById("chatbotToggle");
const chatbotContainer = document.getElementById("chatbotContainer");
const closeChat = document.getElementById("closeChat");

const sendButton = document.getElementById("sendButton");
const chatInput = document.getElementById("chatInput");
const chatDisplay = document.getElementById("chatDisplay");

/* ================= OPEN CHAT ================= */

chatbotToggle.onclick = () => {

    chatbotContainer.style.display = "flex";

    chatbotContainer.animate([
        {
            opacity: 0,
            transform: "translateY(20px) scale(0.9)"
        },
        {
            opacity: 1,
            transform: "translateY(0) scale(1)"
        }
    ],{
        duration: 300,
        easing: "ease"
    });

};

/* ================= CLOSE CHAT ================= */

closeChat.onclick = () => {

    chatbotContainer.style.display = "none";

};

/* ================= SEND BUTTON ================= */

sendButton.onclick = sendMessage;

/* ================= ENTER KEY ================= */

chatInput.addEventListener("keypress", function (e){

    if(e.key === "Enter"){
        sendMessage();
    }

});

/* ================= SEND MESSAGE ================= */

async function sendMessage(){

    const text = chatInput.value.trim();

    if(text === "") return;

    /* USER MESSAGE */

    const userMessage = document.createElement("div");

    userMessage.className = "user-message";

    userMessage.innerText = text;

    chatDisplay.appendChild(userMessage);

    userMessage.animate([
        {
            opacity: 0,
            transform: "translateY(10px)"
        },
        {
            opacity: 1,
            transform: "translateY(0)"
        }
    ],{
        duration: 250
    });

    chatInput.value = "";

    scrollBottom();

    /* LOADING MESSAGE */

    const loadingMessage = document.createElement("div");

    loadingMessage.className = "bot-message loading-message";

    loadingMessage.innerHTML = `
    
        <div class="typing">
        
            <span></span>
            <span></span>
            <span></span>
            
        </div>
    
    `;

    chatDisplay.appendChild(loadingMessage);

    scrollBottom();

    try{

        const response = await fetch("/api/chat", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                message: text
            })

        });

        const data = await response.json();

        /* REMOVE LOADING */

        loadingMessage.remove();

        /* BOT MESSAGE */

        const botMessage = document.createElement("div");

        botMessage.className = "bot-message";

        chatDisplay.appendChild(botMessage);

        /* TYPING EFFECT */

        let i = 0;

        const reply = data.reply || "Xin lỗi, hiện tại tôi chưa thể trả lời 😢";

        const typingEffect = setInterval(() => {

            botMessage.innerHTML += reply.charAt(i);

            i++;

            scrollBottom();

            if(i >= reply.length){

                clearInterval(typingEffect);

            }

        }, 15);

    }catch (error){

        loadingMessage.remove();

        const errorMessage = document.createElement("div");

        errorMessage.className = "bot-message";

        errorMessage.innerHTML = `
        
            ⚠️ Có lỗi xảy ra. Vui lòng thử lại sau.
        
        `;

        chatDisplay.appendChild(errorMessage);

        scrollBottom();

    }

}

/* ================= AUTO SCROLL ================= */

function scrollBottom(){

    chatDisplay.scrollTo({
        top: chatDisplay.scrollHeight,
        behavior: "smooth"
    });

}

const themeToggle = document.getElementById("themeToggle");

themeToggle.onclick = () => {

    chatbotContainer.classList.toggle("dark-chat");

    if(chatbotContainer.classList.contains("dark-chat")){

        themeToggle.innerHTML = "☀️";

    }else{

        themeToggle.innerHTML = "🌙";

    }

};