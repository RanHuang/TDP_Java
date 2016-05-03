clear;

x=[0.025 0.05 0.07 0.1 0.25 0.5 0.75 1 2 5 10];
wifidirect=[4.25 9.664 11.553 10.106 14.752 4.724 4.312 4.965 4.140 3.890 5.885];
bluetooth=[10.294 14.844 18.889 0.434 0.337 0.246 0.244 0.232 0.234 0.228 0.228];

plot(x,bluetooth,'b--','linewidth',1.5);
hold on 
plot(x,wifidirect,'r-','linewidth',1.5);
hold on

ylabel('数据传输率(M/s)')
xlabel('测试文件大小(M)')
%legend('Service Discovery','Autonomous Mode','Active Scan','Persistant Mode');
legend('Bluetooth','Wi-Fi Direct');
title('蓝牙和Wi-Fi Direct数据传输速率');

