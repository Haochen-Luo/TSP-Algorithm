package 西交利物浦.coursework;

public class City {
    private int num, x, y;
    static int count = 0;
    static double[][] dist;

    public City(int num, int x, int y) {
        this.num = num;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return this.num + "";
    }

    public double distance(City b) {
        //to improve the robustness, if the array dist may not be initialized then it calculates the distance directly.
        //If it is not null, we can use this array to store the calculated distance improve efficiency
        if (dist != null) {
            if (dist[num][b.num] != 0) {
                return dist[num][b.num];
            } else {
                int x1 = x - b.x;
                int y1 = y - b.y;
                dist[num][b.num] = Math.sqrt(x1 * x1 + y1 * y1);
                dist[b.num][num] = dist[num][b.num];
                return dist[num][b.num];
            }
        } else {
            int x1 = x - b.x;
            int y1 = y - b.y;
            return Math.sqrt(x1 * x1 + y1 * y1);
        }
    }


}

