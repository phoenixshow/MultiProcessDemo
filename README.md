# MultiProcessDemo
    双进程守护+复活    
    
1. 两个服务，分别开启一个进程互相守护
2. IPC跨进程通信在连接中断时，就会发现异常，发送通知
3. 进程被清理后通过JobScheduler复活
4. 小米手机无法复活