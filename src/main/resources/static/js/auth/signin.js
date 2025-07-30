document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const emailInput = document.getElementById("email");
    const emailError = document.getElementById("emailError");
    const pwInput = document.getElementById("password");
    const pwError = document.getElementById("passwordError");
    const loginError = document.getElementById("loginError");


    // 이메일 형식 체크
    function isEmailFormatValid(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // 포커스 아웃 시 이메일 형식 체크
    emailInput.addEventListener("blur", () => {
        const email = emailInput.value.trim();
        if (email && !isEmailFormatValid(email)) {
            hideError(); // 상단 메시지 숨김
            showFieldError(emailInput, emailError, "올바른 이메일 형식이 아닙니다.");
        } else {
            hideFieldError(emailInput, emailError);
        }
    });


    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = emailInput.value.trim();
        const password = pwInput.value;

        // 1. 이메일 형식 체크
        if (email && !isEmailFormatValid(email)) {
            hideError(); // 상단 메시지 숨김
            showFieldError(emailInput, emailError, "올바른 이메일 형식이 아닙니다.");
            return;
        } else {
            hideFieldError(emailInput, emailError);
        }

        // 2. 이메일 또는 비밀번호 입력 안 한 경우
        if (email === "" || password === "") {
            showError("이메일 또는 비밀번호가 입력되지 않았습니다.");
            return;
        }

        // 3. 서버 요청
        try {
            const res = await fetch("/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });

            if (!res.ok) {
                const data = await res.json();
                // 서버 유효성 오류일 경우
                if (data.code === "VALIDATION_ERROR" && data.data) {
                    if (data.data.email) {
                        hideError();
                        showFieldError(emailInput, emailError, data.data.email);
                    }
                    if (data.data.password) {
                        hideError();
                        showFieldError(pwInput, pwError, data.data.password);
                    }
                } else {
                    showError(data.message);
                }
                return;
            }

            loginError.classList.add("hidden");
            const data = await res.json();
            console.log("로그인 성공:", data);
            window.location.href = "/dashboard";
        } catch (err) {
            console.error("로그인 에러", err);
            showError("서버 오류가 발생했습니다.");
        }
    });

    // 상단 에러 표시
    function showError(msg) {
        const span = loginError.querySelector("span");
        if (span) span.textContent = msg;
        loginError.classList.remove("hidden");
    }

    // 상단 에러 숨기기
    function hideError() {
        const span = loginError.querySelector("span");
        if (span) span.textContent = "";
        loginError.classList.add("hidden");
    }

    // 필드 에러 표시
    function showFieldError(input, errorElement, message) {
        input.classList.add("border-red-500");
        input.classList.remove("border-gray-300");
        const span = errorElement.querySelector("span");
        if (span) span.textContent = message;
        errorElement.classList.remove("hidden");
    }

    // 필드 에러 숨기기
    function hideFieldError(input, errorElement) {
        input.classList.remove("border-red-500");
        input.classList.add("border-gray-300");
        const span = errorElement.querySelector("span");
        if (span) span.textContent = "";
        errorElement.classList.add("hidden");
    }

    emailInput.addEventListener("input", () => {
        hideFieldError(emailInput, emailError);
    });

    pwInput.addEventListener("input", () => {
        hideFieldError(pwInput, pwError);
    });
});