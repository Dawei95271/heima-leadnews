package com.heima.schedule.test;

import org.omg.CORBA.TIMEOUT;

import java.util.Calendar;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @description: jdk 延迟队列
 * @author: 16420
 * @time: 2023/2/4 22:36
 */
public class DelayedTask implements Delayed {

    // 并发任务的执行时间
    // second
    private int executeTime = 0;

    public DelayedTask(int delay){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, delay);
        this.executeTime = (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 获取元素的剩余时间
     * @param unit the time unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        Calendar calendar = Calendar.getInstance();
        return this.executeTime - calendar.getTimeInMillis() / 1000;
    }

    /**
     * 元素排序
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Delayed o) {
        long value = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return value == 0 ? 0 : (value < 0 ? -1 : 1);
    }


    public static void main(String[] args) {

        DelayQueue<DelayedTask> queue = new DelayQueue<>();

        queue.add(new DelayedTask(5));
        queue.add(new DelayedTask(10));
        queue.add(new DelayedTask(15));

        System.out.println(System.currentTimeMillis() / 1000 + "start consume");

        while (queue.size() != 0){
            DelayedTask task = queue.poll();
            if(task != null){

                System.out.println(System.currentTimeMillis() / 1000 + "consume task");

            }

            //每隔一秒消费一次
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }



}
