package kernel.algorithm;

public class RandomDistribution {
    private String _name;

    public RandomDistribution() {}

    public double getProbability() {
        Prob prob = new Prob();
        double ran = Math.random();
        switch ((int)Math.floor(ran * 4)) {
            case 0:
                PoissonDistribution poisson = prob.poisson();
                this._name = "Poisson";
                return poisson.random();
            case 1:
                UniformDistribution uniform = prob.uniform(0, 1);
                this._name = "Uniform";
                return uniform.random();
            case 2:
                BimodalDistribution bimodal = prob.bimodal();
                this._name = "Bimodal";
                return bimodal.random();
        }
        this._name = "Normal";
        NormalDistribution normal = prob.normal();
        return normal.random();
    }

    public String getName() {
        return this._name;
    }
}