<template>
    <div class="text-center py-5">
        <i class="fas fa-check-circle fa-5x text-success mb-4"></i>
        <h1 class="display-4">Đặt hàng thành công!</h1>
        <p class="lead">Cảm ơn bạn đã mua sắm tại NEXVIBE. Mã đơn hàng của bạn là <strong>#1054</strong>.</p>
        <p>Chúng tôi đã gửi một email xác nhận đến hòm thư của bạn.</p>
        <div class="mt-4">
            <router-link to="/products" class="btn btn-dark me-2">Tiếp tục mua sắm</router-link>
            <router-link to="/order-history" class="btn btn-outline-primary">Xem lịch sử đơn hàng</router-link>
        </div>
    </div>
</template>