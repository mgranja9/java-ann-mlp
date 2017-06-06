package com.github.chen0040.mlp.ann;


import com.github.chen0040.mlp.functions.Sigmoid;
import com.github.chen0040.mlp.functions.TransferFunction;

import java.util.ArrayList;
import java.util.List;


//default network assumes input and output are in the range of [0, 1]
public class MLPLayer {
	private TransferFunction transfer = new Sigmoid();
    final List<MLPNeuron> neurons = new ArrayList<>();

    public MLPLayer(int neuron_count, int dimension)
	{
		for(int i=0; i < neuron_count; i++)
		{
			neurons.add(new MLPNeuron(dimension));
		}
	}

    public double[] output(){
        double[] output = new double[neurons.size()];
        for(int i=0; i < output.length; ++i){
            output[i] = neurons.get(i).output;
        }
        return output;
    }

    public double[] setOutput(double[] output){
        for(int i=0; i< neurons.size(); i++)
        {
            neurons.get(i).output = output[i];
            output[i] = output[i];
        }
        return output.clone();
    }

    public void setTransfer(TransferFunction transfer) {
        this.transfer = transfer;
    }

    public TransferFunction getTransfer(){
        return this.transfer;
    }

	public double[] forward_propagate(double[] input)
	{
        double[] output = new double[neurons.size()];
        for(int i=0; i< neurons.size(); i++)
        {
            MLPNeuron neuron= neurons.get(i);
            output[i] = transfer.calculate(neuron.getValue(input));
            neuron.output = output[i];
        }

        return output;
	}
	
	protected void adjust_weights(double learningRate)
	{
        for(int j=0; j< neurons.size(); j++)
        {
            MLPNeuron neuron_j = neurons.get(j);
            int dimension = neuron_j.dimension();
            for(int i=0; i < dimension; ++i) {

                double dE_dzj = neuron_j.dE_dzj;

                double w_ji = neuron_j.getWeight(i);

                double yi = neuron_j.values[i];
                double dw = learningRate * dE_dzj * yi;
                w_ji += dw;
                neuron_j.setWeightDelta(i, dw);
                neuron_j.setWeight(i, w_ji);
            }
        }

        for(int j=0; j < neurons.size(); j++)
        {
            MLPNeuron neuron = neurons.get(j);
            double sink_w0 = neuron.bias_weight;
            double dE_dzj = neuron.dE_dzj;
            sink_w0 += learningRate * dE_dzj;
            neuron.bias_weight = sink_w0;
        }
	}

    private int dimension(){
        return neurons.get(0).dimension();
    }
	
	public double[] back_propagate(double[] dE_dyj)
	{
        for(int i=0; i< neurons.size(); i++)
        {
            MLPNeuron neuron= neurons.get(i);
            double[] values = neuron.values;
            double zj = neuron.getValue(values);
            neuron.dE_dzj = transfer.gradient(zj) * dE_dyj[i];
        }

        int dimension = dimension();
        double[] dE_dyi = new double[dimension];
        for(int i = 0; i < dimension; ++i) {
            double error_sum = 0;

            for (int j = 0; j < neurons.size(); j++) {
                MLPNeuron neuron = neurons.get(j);
                double w_ji = neuron.getWeight(i);
                double dE_dzj = neuron.dE_dzj;
                error_sum += (w_ji * dE_dzj);
            }
            dE_dyi[i] = error_sum;
        }

        return dE_dyi;
	}


}
