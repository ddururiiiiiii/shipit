document.addEventListener("DOMContentLoaded", () => {

    loadTeamTypes();
    loadRoleTypes();
    const emailInput = document.getElementById("email");
    const emailError = document.getElementById("emailError");

    const pwInput = document.getElementById("password");
    const pwError = document.getElementById("passwordError");

    const pwConfirm = document.getElementById("passwordConfirm");
    const pwConfirmError = document.getElementById("passwordConfirmError");

    const nameInput = document.getElementById("name");
    const nameError = document.getElementById("nameError");

    const teamSelect = document.getElementById("team");
    const teamError = document.getElementById("teamError");

    const roleSelect = document.getElementById("role");
    const roleError = document.getElementById("roleError");

    let emailVerified = false;

    // ========== 🔍 유효성 검사 함수들 ==========
    function isEmailFormatValid(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    function isPasswordStrong(pw) {
        const lengthCheck = pw.length >= 8 && pw.length <= 20;
        const hasUpper = /[A-Z]/.test(pw);
        const hasLower = /[a-z]/.test(pw);
        const hasNumber = /[0-9]/.test(pw);
        const hasSpecial = /[^a-zA-Z0-9]/.test(pw);
        return lengthCheck && hasUpper && hasLower && hasNumber && hasSpecial;
    }

    // ========== 🧠 실시간 포커스 아웃 유효성 ==========
    pwInput.addEventListener("blur", () => {
        const pw = pwInput.value.trim();

        if (pw === "") {
            showError(pwInput, pwError, "비밀번호 입력은 필수입니다.");
        } else if (!isPasswordStrong(pw)) {
            showError(pwInput, pwError, "8~20자 이내 영문 대/소문자, 숫자, 특수문자가 모두 포함되어야 합니다.");
        } else {
            hideError(pwInput, pwError);
        }
    });

    pwConfirm.addEventListener("blur", () => {
        if (pwInput.value && pwConfirm.value) {
            if (pwInput.value !== pwConfirm.value) {
                showError(pwConfirm, pwConfirmError, "입력한 비밀번호와 일치하지 않습니다.");
            } else {
                showSuccess(pwConfirm, pwConfirmError, "입력한 비밀번호와 일치합니다.");
            }
        } else {
            hideError(pwConfirm, pwConfirmError);
        }
    });

    // ========== ✅ 이메일 인증 버튼 클릭 ==========
    window.sendVerification = async function () {
        const email = emailInput.value.trim();

        if (email === "") {
            showError(emailInput, emailError, "이메일이 입력되지 않았습니다.");
            return;
        }

        if (!isEmailFormatValid(email)) {
            showError(emailInput, emailError, "올바른 이메일 형식이 아닙니다.");
            return;
        }

        try {
            const res = await fetch("/api/email-verification/send", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email })
            });

            if (!res.ok) {
                const data = await res.json();
                alert(data.message || "인증 요청 중 오류가 발생했습니다.");
                return;
            }
            alert("인증 이메일이 전송되었습니다. 이메일을 확인해주세요.");
        } catch (error) {
            console.error("인증 요청 실패", error);
            alert("인증 요청 중 오류가 발생했습니다.");
        }
    };


    // ========== 🔒 폼 제출 시 전체 유효성 검사 ==========
    document.getElementById("signupForm").addEventListener("submit", async function (e) {
        e.preventDefault();
        let hasError = false;
        const email = emailInput.value.trim();

        const submitButton = document.querySelector("#signupForm button[type='submit']");
        submitButton.disabled = true;

        // 이메일 검사
        if (email === "") {
            showError(emailInput, emailError, "이메일 입력은 필수 입니다.");
            hasError = true;
        } else if (!isEmailFormatValid(email)) {
            showError(emailInput, emailError, "올바른 이메일 형식이 아닙니다.");
            hasError = true;
        } else {
            hideError(emailInput, emailError);
        }

        // 이메일 인증 여부 확인
        if (!emailVerified) {
            showError(emailInput, emailError, "이메일 인증을 완료해주세요.");
            hasError = true;
        }

        // 비밀번호
        const pw = pwInput.value.trim();
        if (pw === "") {
            showError(pwInput, pwError, "비밀번호 입력은 필수입니다.");
            hasError = true;
        } else if (!isPasswordStrong(pw)) {
            showError(pwInput, pwError, "8~20자 이내 영문 대/소문자, 숫자, 특수문자가 모두 포함되어야 합니다.");
            hasError = true;
        } else {
            hideError(pwInput, pwError);
        }

        // 비밀번호 확인
        const pwC = pwConfirm.value.trim();
        if (pw && pwC) {
            if (pw !== pwC) {
                showError(pwConfirm, pwConfirmError, "입력한 비밀번호와 일치하지 않습니다.");
                hasError = true;
            } else {
                showSuccess(pwConfirm, pwConfirmError, "입력한 비밀번호와 일치합니다.");
            }
        } else {
            hideError(pwConfirm, pwConfirmError);
        }

        // 이름
        if (nameInput.value.trim() === "") {
            showError(nameInput, nameError, "이름 입력은 필수 입니다.");
            hasError = true;
        } else {
            hideError(nameInput, nameError);
        }

        // 팀
        if (teamSelect.value === "") {
            showError(teamSelect, teamError, "팀 선택은 필수 입니다.");
            hasError = true;
        } else {
            hideError(teamSelect, teamError);
        }

        // 권한
        if (roleSelect.value === "") {
            showError(roleSelect, roleError, "권한 선택은 필수 입니다.");
            hasError = true;
        } else {
            hideError(roleSelect, roleError);
        }

        if (hasError) {
            submitButton.disabled = false;
            return;
        }

        // 서버에 제출
        const userData = {
            email: email,
            password: pw,
            username: nameInput.value.trim(),
            team: teamSelect.value || null,
            role: roleSelect.value || null,
        };

        try {
            const res = await fetch("/api/users", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userData)
            });

            if (!res.ok) {
                const err = await res.json();

                if (err.code === "VALIDATION_ERROR" && err.data) {
                    if (err.data.email) showError(emailInput, emailError, err.data.email);
                    if (err.data.password) showError(pwInput, pwError, err.data.password);
                    if (err.data.username) showError(nameInput, nameError, err.data.username);
                    if (err.data.team) showError(teamSelect, teamError, err.data.team);
                    if (err.data.role) showError(roleSelect, roleError, err.data.role);
                } else {
                    alert("회원가입 실패: " + (err.message || "서버 오류"));
                }
                return;
            }

            alert("회원가입 성공! 로그인 화면으로 이동합니다.");
            window.location.href = "/auth/signin";
        } catch (err) {
            console.error("회원가입 중 오류", err);
            alert("서버 오류가 발생했습니다.");
        }
    });

    // ========== 🔧 헬퍼 함수 ==========
    function showError(input, errorElement, message) {
        input.classList.remove("border-green-500");
        input.classList.add("border-red-500");

        const span = errorElement.querySelector("span");
        if (span) span.textContent = message;

        errorElement.classList.remove("hidden", "text-green-600");
        errorElement.classList.add("text-red-600");
    }

    function showSuccess(input, errorElement, message) {
        input.classList.remove("border-red-500");
        input.classList.add("border-green-500");

        const span = errorElement.querySelector("span");
        if (span) span.textContent = message;

        errorElement.classList.remove("hidden", "text-red-600");
        errorElement.classList.add("text-green-600");
    }

    function hideError(input, errorElement) {
        input.classList.remove("border-red-500", "border-green-500");

        const span = errorElement.querySelector("span");
        if (span) span.textContent = "";

        errorElement.classList.add("hidden");
    }

    emailInput.addEventListener("input", () => hideError(emailInput, emailError));
    pwInput.addEventListener("input", () => hideError(pwInput, pwError));
    pwConfirm.addEventListener("input", () => hideError(pwConfirm, pwConfirmError));
    nameInput.addEventListener("input", () => hideError(nameInput, nameError));
    teamSelect.addEventListener("change", () => hideError(teamSelect, teamError));
    roleSelect.addEventListener("change", () => hideError(roleSelect, roleError));

});


async function loadTeamTypes() {
    try {
        const res = await fetch("/api/enums/team-types");
        if (!res.ok) throw new Error("불러오기 실패");

        const teamList = await res.json();
        teamList.sort((a, b) => a.label.localeCompare(b.label));
        const teamSelect = document.getElementById("team");
        teamSelect.innerHTML = '<option value="">-- 팀 선택 --</option>';

        teamList.forEach(team => {
            const option = document.createElement("option");
            option.value = team.name;
            option.textContent = team.label;
            teamSelect.appendChild(option);
        });
    } catch (err) {
        console.error("팀 목록 로드 실패", err);
    }
}
async function loadRoleTypes() {
    try {
        const res = await fetch("/api/enums/role-types");
        if (!res.ok) throw new Error("불러오기 실패");

        const roleList = await res.json();
        roleList.sort((a, b) => a.label.localeCompare(b.label));

        const roleSelect = document.getElementById("role");
        roleSelect.innerHTML = '<option value="">-- 권한 선택 --</option>';

        roleList.forEach(role => {
            const option = document.createElement("option");
            option.value = role.name;
            option.textContent = role.label;
            roleSelect.appendChild(option);
        });
    } catch (err) {
        console.error("권한 목록 로드 실패", err);
    }
}

