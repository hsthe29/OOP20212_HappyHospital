package game.classes.statistic;

import game.classes.Text;
import game.constant.Constant;

import java.util.*;

public class Forcasting {
    private Map<Integer, HashSet<WaitingDuration>> waitingAutoAgv;
    private Set<Integer> doneAutoAgv;
    private int doNothing = 0;
    public int averageAverageWaitingTime = 0;

    public Forcasting() {
        this.waitingAutoAgv = new HashMap<Integer, HashSet<WaitingDuration>>();
        this.doneAutoAgv = new HashSet<Integer>();
    }

    public void rememberDoneAutoAgv(Integer id) {
        if (this.doneAutoAgv == null) {
            this.doneAutoAgv = new HashSet<Integer>();
        }
        this.doneAutoAgv.add(id);
    }

    public void removeAutoAgv(Integer id) {
        if (this.waitingAutoAgv == null) {
            return;
        }

        if (this.waitingAutoAgv.size() == 0) {
            return;
        }

        this.waitingAutoAgv.remove(id);
    }

    public void removeDuration(Integer id) {
        if (this.waitingAutoAgv == null) {
            return;
        }

        if (this.waitingAutoAgv.containsKey(id)) {
            int now = (int) Math.floor(/*performance.now() / 1000*/ 7);
            ArrayList<WaitingDuration> arr = new ArrayList<>();
            this.waitingAutoAgv.get(id).forEach((item) -> {
                if (item.end != -1 && item.end < now - Constant.DELTA_T) {
                    arr.add(item);
                }
            });
            arr.forEach((item) -> {
                this.waitingAutoAgv.get(id).remove(item);
            });

            if (this.waitingAutoAgv.get(id).size() == 0) {
                if (this.doneAutoAgv.contains(id)) {
                    this.waitingAutoAgv.remove(id);
                    this.doneAutoAgv.remove(id);
                }
            }
            arr.clear();
        }
    }

    public void addDuration(Integer id, WaitingDuration duration) {
        if (this.waitingAutoAgv == null) {
            this.waitingAutoAgv = new HashMap<>();
        }

        if (!this.waitingAutoAgv.containsKey(id)) {
            this.waitingAutoAgv.put(id, new HashSet<>());
        }

        HashSet<WaitingDuration> m = this.waitingAutoAgv.get(id);
        m.add(duration);
        this.waitingAutoAgv.put(id, m);
    }

    public void updateDuration(int id, int begin, int end) {
        if (this.waitingAutoAgv == null) {
            return;
        }
        if (this.waitingAutoAgv.containsKey(id)) {
            this.waitingAutoAgv.get(id).forEach((item) -> {
                if (item.begin == begin) {
                    item.end = end;
                    item.duration = item.end - item.begin;
                }
            });
        }
    }

    public int totalAverageWaitingTime() {
        int result = 0;
        if (this.waitingAutoAgv == null) {
            this.waitingAutoAgv = new HashMap<Integer, HashSet<WaitingDuration>>();
            return 0;
        }
        if (this.waitingAutoAgv.size() == 0) {
            return 0;
        }
        int now = (int) Math.floor(/*performance.now() / 1000*/ 5);
        for (int key : this.waitingAutoAgv.keySet()) {
            Set<WaitingDuration> value = this.waitingAutoAgv.get(key);
            int average = 0;
            int count = 0;
            this.removeDuration(key);
            for (WaitingDuration item : value) {
                count++;
                if (item.end == -1) {
                    average += now - item.begin;
                } else {
                    average += item.duration;
                }
            }
            if (count == 0) {
                average = 0;
            } else {
                average = average / count;
            }
            result += average;
        }
        result = (int) Math.floor(result * 100) / 100;
        return result;
    }

    public void log(Text text) {
        int total = this.totalAverageWaitingTime();
        int numAutoAgv = this.waitingAutoAgv.size();
        int result = 0;
        if (numAutoAgv != 0) {
            result = total / numAutoAgv;
        }
        result = (int) Math.floor(result * 100) / 100;
//        text.setText("Tu giay: " + Math.floor((performance.now() / 1000) - Constant.DELTA_T)
//                + ", #AutoAgv: " + numAutoAgv + " totalTime: " +
//                total + " avg: " + result + "#Stop: " + this.waitingAutoAgv.get(2).size());

    }

    public void calculate() {
        int total = this.totalAverageWaitingTime();
        int numAutoAgv = this.waitingAutoAgv.size();
        if (numAutoAgv != 0) {
            this.averageAverageWaitingTime = total / numAutoAgv;
        }
        this.averageAverageWaitingTime = (int) Math.floor(this.averageAverageWaitingTime * 100) / 100;
    }
}