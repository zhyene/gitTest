package com.zhhs.commonPack;

import com.zhhs.commonPack.utils.JodaTimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Fork Join Pool 测试
 * 支持并发，异步执行，提供时间间隔
 */
public class Test {

    static Map<String, Integer> topLevelDomain = new HashMap<>();
    static {
        topLevelDomain.put("com", 1);
        topLevelDomain.put("com.net", 1);
        topLevelDomain.put("gz.net", 1);
        topLevelDomain.put("com.net", 1);
        topLevelDomain.put("gz.net", 1);
        topLevelDomain.put("com.net", 1);
        topLevelDomain.put("gz.net", 1);
        topLevelDomain.put("com.net", 1);
        topLevelDomain.put("gz.net", 1);
        topLevelDomain.put("gz11.net", 1);
        topLevelDomain.put("gz12.net", 1);
        topLevelDomain.put("gz13.net", 1);
        topLevelDomain.put("gz11.net", 1);
        topLevelDomain.put("gz12.net", 1);
        topLevelDomain.put("gz13.net", 1);
        topLevelDomain.put("gz11.net", 1);
        topLevelDomain.put("gz12.net", 1);
        topLevelDomain.put("gz131.net", 1);
        topLevelDomain.put("gz111.net", 1);
        topLevelDomain.put("gz121.net", 1);
        topLevelDomain.put("gz1322.net", 1);
        topLevelDomain.put("gz112.net", 1);
        topLevelDomain.put("gz125.net", 1);
        topLevelDomain.put("gz136.net", 1);
        topLevelDomain.put("gz117.net", 1);
        topLevelDomain.put("gz128.net", 1);
        topLevelDomain.put("gz139.net", 1);
        topLevelDomain.put("gz11a.net", 1);
        topLevelDomain.put("gz12d.net", 1);
        topLevelDomain.put("gz13c.net", 1);
        topLevelDomain.put("gz11r.enet", 1);
        topLevelDomain.put("gz12e.net", 1);
        topLevelDomain.put("gz13s.net", 1);
    }

    static class DomainRecursiveTask extends RecursiveTask<Map<String, Integer>> {

        private String domain;

        DomainRecursiveTask(String domain){
            this.domain = domain;
        }

        @Override
        protected Map<String, Integer> compute() {
            Map<String,Integer> res = new HashMap<>();
            res.put(domain,-2);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return res;
        }
    }

    static class ForkRecursiveTask extends RecursiveTask<Map<String, Integer>> {

        private Map<String, Integer> domainMap;

        public ForkRecursiveTask(Map<String, Integer> domainMap) {
            this.domainMap = domainMap;
        }

        @Override
        protected Map<String, Integer> compute() {
            List<ForkJoinTask<Map<String, Integer>>> tasks = new ArrayList<>();
            Map<String, Integer> result = new HashMap<>();

            for (String key : domainMap.keySet()) {
                DomainRecursiveTask task = new DomainRecursiveTask(key);
                tasks.add(task.fork());
            }

            for (ForkJoinTask<Map<String, Integer>> task : tasks) {
                Map<String, Integer> map = task.join();
                result.putAll(map);
                System.out.println("Thread-"+Thread.currentThread() + "  ==> "
                        + JodaTimeUtil.currentDate("yyyy-MM-dd hh:mm:ss.SSS"));
            }
            return result;
        }
    }

    public static void main(String[] args) throws IOException {

        ForkJoinPool forkJoinPool = new ForkJoinPool(11);
        Map<String, Integer> map = forkJoinPool.invoke(new ForkRecursiveTask(topLevelDomain));
        //输出最终所有的计算结果
        for (String key : map.keySet()) {
            System.out.println(key + "=" + map.get(key));
        }
    }
}