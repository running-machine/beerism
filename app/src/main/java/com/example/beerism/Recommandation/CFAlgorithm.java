package com.example.beerism.Recommandation;

import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 참고 url : https://kutar37.tistory.com/39

public class CFAlgorithm {
    String TAG = "ObjectDetection";
    private Map<String, Map<String, Double>> critics;

    private static CFAlgorithm instance = new CFAlgorithm();

    public static CFAlgorithm getInstance() {
        return instance;
    }

    private CFAlgorithm() {
        critics = new HashMap<>();
    }

    public void inputData(String name, Map<String, Double>  movieInfo) {
        critics.put(name, movieInfo);
    }
    public Map<String, Map<String, Double>> getData() {return critics;}
    public void initData() {
        critics.clear();
    }

    public double calculatePearsonNum(String name1, String name2) {
        double sumX = 0, sumY = 0, sumPowX = 0, sumPowY = 0, sumXY = 0, count = 0;

        for (String key : critics.get(name1).keySet()) {
            if (critics.get(name2).containsKey(key)) {
                sumX += critics.get(name1).get(key);
                sumY += critics.get(name2).get(key);
                sumPowX += Math.pow(critics.get(name1).get(key), 2);
                sumPowY += Math.pow(critics.get(name2).get(key), 2);
                sumXY += critics.get(name1).get(key) * critics.get(name2).get(key);
                count++;
            }
        }

        return (sumXY - ((sumX * sumY) / count)) / Math.sqrt((sumPowX - (Math.pow(sumX, 2) / count)) * (sumPowY - (Math.pow(sumY, 2) / count)));
    }

    // 각 사람들에 대한 유사도
    public List<Map.Entry<String, Double>> topMatch(String name) {
        List<Map.Entry<String, Double>> answer = new ArrayList<>();

        for (String key : critics.keySet()) {
            if (!key.equals(name)) {
                answer.add(new AbstractMap.SimpleEntry(key, calculatePearsonNum(name, key)));
            }
        }

        answer.sort((o1, o2) -> {
            double score1 = o1.getValue(), score2 = o2.getValue();

            if (score1 == score2) return 0;
            else if (score1 > score2) return -1;
            else return 1;
        });

        return answer;
    }

    public List<Map.Entry<String, Double>> getRecommandation(String name) {
        List<Map.Entry<String, Double>> answer = topMatch(name);

        double simNum = 0, score = 0;
        List<Map.Entry<String, Double>> recommandationLst = new ArrayList<>();
        Map<String, Double> scoreMap = new HashMap<>();
        Map<String, Double> simMap = new HashMap<>();

        for (Map.Entry<String, Double> sim: answer) {
            if (sim.getValue() < 0) continue;

            for (String beerName : critics.get(sim.getKey()).keySet()) {
                if (!critics.get(name).containsKey(beerName)) {
                    score += sim.getValue() * critics.get(sim.getKey()).get(beerName);

                    scoreMap.put(beerName, scoreMap.containsKey(beerName) ? scoreMap.get(beerName) + score : score);
                    simMap.put(beerName, simMap.containsKey(beerName) ? simMap.get(beerName) + sim.getValue() : sim.getValue());
                }

                score = 0;
            }
        }

        for (String key : scoreMap.keySet()) {
            scoreMap.put(key, scoreMap.get(key) / simMap.get(key));
            recommandationLst.add(new AbstractMap.SimpleEntry(key, scoreMap.get(key)));
        }

        recommandationLst.sort((o1, o2) -> {
            double score1 = o1.getValue(), score2 = o2.getValue();

            if (score1 == score2) return 0;
            else if (score1 > score2) return -1;
            else return 1;
        });

        return recommandationLst;
    }

//    public void main() {
//        inputData();
//
//        List<Map.Entry<String, Double>> ranks = topMatch("박병관");
//
//        Log.d(TAG, "유사도");
//        for (Map.Entry<String, Double> item : ranks) {
////            Log.d(TAG, item.getKey() + "'s score = " + item.getValue());
//        }
//
//        Log.d(TAG, "예상 평점");
//        List<Map.Entry<String, Double>> scores = getRecommandation("박병관");
//        for (Map.Entry<String, Double> item : scores) {
//            Log.d(TAG, item.getKey() + "'s score = " + item.getValue());
//        }
//    }
}