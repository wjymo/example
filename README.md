authorization-server为认证服务器，clients为客户端，都是最简配置，只为测试实现效果。
authorization-server使用springsecurityoauth2框架来做认证和token的分发，token使用jwt，为了使token有状态，使用redis作为存储