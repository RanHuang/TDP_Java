% Matlab求方差，均值，均方差，协方差的函数
% http://www.cnblogs.com/linkr/articles/2297783.html


clc;
clear;
x=[1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30];
y_d2d_connect = [12.153 11.872 8.699 11.056 9.635 10.394 9.901 12.809 7.981 10.492 8.696 7.883 8.446 9.187 8.050 11.337 9.657 8.260 9.468 8.504 10.320 7.912 9.218 8.630 8.506 7.987 7.988 10.327 9.476 7.648];
num = length(y_d2d_connect);
y_d2d = zeros(1,num);
for i = 1:num
    y_d2d(i) = y_d2d_connect(i) + 2.005;
end

y_wifi= [9.861 6.171 5.935 4.658 4.913 6.129 5.206 6.551 7.745 5.007 3.898 5.746 6.044 11.162 6.720 7.170 5.245 6.990 5.023 5.923 5.317 5.843 4.803 6.077 5.368 6.813 5.758 7.445 9.572 4.634];

d2d_average = mean(y_d2d);
d2d_standard_deviation = var(y_d2d);

wifi_average = mean(y_wifi);
wifi_standard_deviation = var(y_wifi);

%绘制散点图
scatter(x,y_d2d,'r*');
hold on
scatter(x,y_wifi,'b+');

ylabel('\fontsize {8}时间(秒)')
xlabel('\fontsize {8}配对次数')
%legend('Service Discovery','Autonomous Mode','Active Scan','Persistant Mode');
legend('\fontsize {8}用户透明的安全配对模式','\fontsize {8}Wi-Fi Direct原生安全配对模式');
title('\fontsize {8}设备初次相遇安全配对时间');