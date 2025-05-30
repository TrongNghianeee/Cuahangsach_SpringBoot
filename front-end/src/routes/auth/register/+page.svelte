<script>
    import { goto } from '$app/navigation';

    let username = '';
    let password = '';
    let confirmPassword = '';
    let email = '';
    let fullName = '';
    let phone = '';
    let address = '';
    let error = '';
    let success = '';
    let loading = false;

    function validateForm() {
        if (!username.trim()) {
            error = 'Vui lòng nhập tên người dùng';
            return false;
        }
        if (username.length < 3) {
            error = 'Tên người dùng phải có ít nhất 3 ký tự';
            return false;
        }
        if (!password.trim()) {
            error = 'Vui lòng nhập mật khẩu';
            return false;
        }
        if (password.length < 6) {
            error = 'Mật khẩu phải có ít nhất 6 ký tự';
            return false;
        }
        if (password !== confirmPassword) {
            error = 'Mật khẩu xác nhận không khớp';
            return false;
        }
        if (!email.trim()) {
            error = 'Vui lòng nhập email';
            return false;
        }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            error = 'Email không hợp lệ';
            return false;
        }
        if (!fullName.trim()) {
            error = 'Vui lòng nhập họ tên';
            return false;
        }
        return true;
    }

    async function handleRegistry() {
        if (!validateForm()) {
            return;
        }

        loading = true;
        error = '';
        success = '';

        try {
            const response = await fetch('http://localhost:8080/api/auth/registry', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password, email, fullName, phone, address })
            });

            if (response.ok) {
                const data = await response.json();
                success = data.message || 'Đăng ký thành công! Đang chuyển hướng...';
                error = '';
                setTimeout(() => goto('/auth/login'), 2000); // Chuyển hướng sau 2 giây
            } else {
                const data = await response.json();
                error = data.error || 'Đăng ký thất bại';
                success = '';
            }
        } catch (e) {
            error = 'Lỗi kết nối. Vui lòng thử lại.';
            success = '';
        } finally {
            loading = false;
        }
    }
</script>

<div class="min-h-screen bg-gradient-to-br from-green-50 to-emerald-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
        <div class="text-center">
            <div class="mx-auto h-16 w-16 bg-green-600 rounded-full flex items-center justify-center mb-4">
                <svg class="h-8 w-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                </svg>
            </div>
            <h2 class="text-3xl font-bold text-gray-900 mb-2">Bookstore</h2>
            <p class="text-gray-600">Tạo tài khoản mới</p>
        </div>

        <div class="bg-white rounded-lg shadow-lg p-8">
            {#if error}
                <div class="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
                    {error}
                </div>
            {/if}

            {#if success}
                <div class="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded-md">
                    {success}
                </div>
            {/if}

            <form on:submit|preventDefault={handleRegistry} class="space-y-4">
                <div class="grid grid-cols-1 gap-4">
                    <div>
                        <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
                            Tên người dùng <span class="text-red-500">*</span>
                        </label>
                        <input
                            id="username"
                            type="text"
                            bind:value={username}
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                            placeholder="Nhập tên người dùng"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div>
                        <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                            Email <span class="text-red-500">*</span>
                        </label>
                        <input
                            id="email"
                            type="email"
                            bind:value={email}
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                            placeholder="Nhập email"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div>
                        <label for="fullName" class="block text-sm font-medium text-gray-700 mb-1">
                            Họ tên <span class="text-red-500">*</span>
                        </label>
                        <input
                            id="fullName"
                            type="text"
                            bind:value={fullName}
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                            placeholder="Nhập họ tên"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div>
                        <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">
                            Số điện thoại
                        </label>
                        <input
                            id="phone"
                            type="tel"
                            bind:value={phone}
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                            placeholder="Nhập số điện thoại"
                            disabled={loading}
                        />
                    </div>

                    <div>
                        <label for="address" class="block text-sm font-medium text-gray-700 mb-1">
                            Địa chỉ
                        </label>
                        <textarea
                            id="address"
                            bind:value={address}
                            rows="2"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                            placeholder="Nhập địa chỉ"
                            disabled={loading}
                        ></textarea>
                    </div>

                    <div>
                        <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
                            Mật khẩu <span class="text-red-500">*</span>
                        </label>
                        <input
                            id="password"
                            type="password"
                            bind:value={password}
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                            placeholder="Nhập mật khẩu (ít nhất 6 ký tự)"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div>
                        <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
                            Xác nhận mật khẩu <span class="text-red-500">*</span>
                        </label>
                        <input
                            id="confirmPassword"
                            type="password"
                            bind:value={confirmPassword}
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                            placeholder="Nhập lại mật khẩu"
                            required
                            disabled={loading}
                        />
                    </div>
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    class="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed mt-6"
                >
                    {#if loading}
                        <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Đang đăng ký...
                    {:else}
                        Đăng ký
                    {/if}
                </button>
            </form>

            <div class="mt-6 text-center">
                <p class="text-sm text-gray-600">
                    Đã có tài khoản? 
                    <a href="/auth/login" class="font-medium text-green-600 hover:text-green-500 hover:underline">
                        Đăng nhập ngay
                    </a>
                </p>
            </div>
        </div>
    </div>
</div>