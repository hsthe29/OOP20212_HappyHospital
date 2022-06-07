package game.algorithm;

import game.constant.Constant;

public class Prob {

    // default arguments
    public UniformDistribution uniform() {
        return new UniformDistribution( 0, 1);
    }

    public NormalDistribution normal() {
        return new NormalDistribution(0, 1);
    }

    public ExponentialDistribution exponential() {
        return new ExponentialDistribution(1);
    }

    public LogNormalDistribution logNormal() {
        return new LogNormalDistribution(0, 1);
    }

    public PoissonDistribution poisson() {
        return new PoissonDistribution(1);
    }

    public BimodalDistribution bimodal() {
        return new BimodalDistribution(1);
    }

    //
    public UniformDistribution uniform(double min, double max) {
        return new UniformDistribution(min, max);
    }

    public NormalDistribution normal(double mean, double sd) {
        return new NormalDistribution(mean, sd);
    }

    public ExponentialDistribution exponential(double lambda) {
        return new ExponentialDistribution(lambda);
    }

    public LogNormalDistribution logNormal(double mu, double sigma) {
        return new LogNormalDistribution(mu, sigma);
    }

    public PoissonDistribution poisson(double lambda) {
        return new PoissonDistribution(lambda);
    }

    public BimodalDistribution bimodal(double lambda) {
        return new BimodalDistribution(lambda);
    }

}
enum DistributionType {
    Unknown,
    Continuous,
    Discrete
}

interface Distribution {
    double min = 0;
    double max = 0;
    double mean = 0;
    double variance = 0;
    DistributionType type = DistributionType.Unknown;

    double random();
}

class UniformDistribution implements Distribution {

    private double _min;
    private double _max;
    private double _range;
    private double _mean;
    private double _variance;
    private DistributionType _type;

    public UniformDistribution(double min, double max) {
        this._min = min;
        this._max = max;
        this._range = max - min;
        this._mean = min + this._range / 2;
        this._variance = ((max - min) * (max - min)) / 12;
        this._type = DistributionType.Continuous;
    }

    public double getMin() {
        return this._min;
    }

    public double getMax() {
        return this._max;
    }

    public double getMean() {
        return this._mean;
    }

    public double getVariance() {
        return this._variance;
    }

    public DistributionType getType() {
        return this._type;
    }

    @Override
    public double random() {
        return this._min + Constant.rng01() * this._range;
    }
}

class NormalDistribution implements Distribution {

    private double _min;
    private double _max;
    private double _mean;
    private double _sd;
    private double _variance;
    private DistributionType _type;
    private double _y1;
    private double _y2;

    public NormalDistribution(double mean, double sd) {
        this._min = Double.NEGATIVE_INFINITY;
        this._max = Double.POSITIVE_INFINITY;
        this._mean = mean;
        this._sd = sd;
        this._variance = sd * sd;
        this._type = DistributionType.Continuous;
    }

    public double getMin() {
        return this._min;
    }

    public double getMax() {
        return this._max;
    }

    public double getMean() {
        return this._mean;
    }

    public double getVariance() {
        return this._variance;
    }

    public DistributionType getType() {
        return this._type;
    }

    @Override
    public double random() {
        double M = 1.0 / (this._sd * Math.sqrt(Math.PI * 2));
        double x = Constant.rng11() - this._mean;
        double w = Math.exp(- x * x / (2 * this._variance));
        return M * w;
    }

}

class ExponentialDistribution implements Distribution {

    private double _min;
    private double _max;
    private double _mean;
    private double _variance;
    private DistributionType _type;

    public ExponentialDistribution(double lambda) {
        this._min = 0;
        this._max = Double.POSITIVE_INFINITY;
        this._mean = 1.0 / lambda;
        this._variance = 1.0 / (lambda * lambda);
        this._type = DistributionType.Continuous;
    }

    public double getMin() {
        return this._min;
    }

    public double getMax() {
        return this._max;
    }

    public double getMean() {
        return this._mean;
    }

    public double getVariance() {
        return this._variance;
    }

    public DistributionType getType() {
        return this._type;
    }

    @Override
    public double random() {
        return -1 * Math.log(Constant.rng01()) * this._mean;
    }
}

class LogNormalDistribution implements Distribution {

    private double _min;
    private double _max;
    private double _mean;
    private double _variance;
    private DistributionType _type;
    private NormalDistribution _nf;

    public LogNormalDistribution(double mu, double sigma) {
        this._min = 0;
        this._max = Double.POSITIVE_INFINITY;
        this._mean = Math.exp(mu + (sigma * sigma) / 2);
        this._variance = (Math.exp(sigma * sigma) - 1) * Math.exp(2 * mu + sigma * sigma);
        this._type = DistributionType.Continuous;
        this._nf = new NormalDistribution(mu, sigma);
    }

    public double getMin() {
        return this._min;
    }

    public double getMax() {
        return this._max;
    }

    public double getMean() {
        return this._mean;
    }

    public double getVariance() {
        return this._variance;
    }

    public DistributionType getType() {
        return this._type;
    }

    @Override
    public double random() {
        return Math.exp(this._nf.random());
    }
}

class PoissonDistribution implements Distribution {

    private double _min;
    private double _max;
    private double _mean;
    private double _variance;
    private DistributionType _type;
    private double _L;

    public PoissonDistribution(double lambda) {
        this._min = 0;
        this._max = Double.POSITIVE_INFINITY;
        this._mean = lambda;
        this._variance = lambda;
        this._type = DistributionType.Discrete;
        this._L = Math.exp(-lambda);
    }

    public double getMin() {
        return this._min;
    }

    public double getMax() {
        return this._max;
    }

    public double getMean() {
        return this._mean;
    }

    public double getVariance() {
        return this._variance;
    }

    public DistributionType getType() {
        return this._type;
    }

    @Override
    public double random() {
        int k = 0;
        double p = 1.0;
        while (true) {
            p = p * Constant.rng01();
            if(p < this._L)
                break;
            k++;
        }
        return p;
    }
}

class BimodalDistribution implements Distribution {

    private double _min;
    private double _max;
    private double _mean;
    private double _variance;
    private DistributionType _type;
    private double _p;

    public BimodalDistribution(double lambda) {
        this._min = 0;
        this._max = Double.POSITIVE_INFINITY;
        this._mean = lambda;
        this._variance = lambda;
        this._type = DistributionType.Discrete;

        double abs = Math.abs(lambda);
        if(abs < 1.0 && abs != 0.0)
            this._p = abs;
        else this._p = (abs == 0.0)? 0.6 : 1/abs;
    }

    public double getMin() {
        return this._min;
    }

    public double getMax() {
        return this._max;
    }

    public double getMean() {
        return this._mean;
    }

    public double getVariance() {
        return this._variance;
    }

    public DistributionType getType() {
        return this._type;
    }

    @Override
    public double random() {
        double N = 3628800;
        double x = Math.floor(Constant.rng01() * 9);
        double px = Math.pow(this._p, x);
        double qx = Math.pow(1 - this._p, 10 - x);
        double M = 1;
        for(int i = 1; i <= x; i++) {
            M = M * i * (10 - i);
        }
        return (N / M) * px * qx;
    }
}
