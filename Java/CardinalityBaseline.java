package mulan.ASBCT;

import mulan.classifier.MultiLabelLearnerBase;
import mulan.classifier.MultiLabelOutput;
import mulan.data.MultiLabelInstances;
import weka.core.Instance;
import weka.core.TechnicalInformation;

import java.util.Arrays;
import java.util.HashMap;

public class CardinalityBaseline extends MultiLabelLearnerBase {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private boolean[] output;

    /*
     * @return a hash map associating each of the following measures with
     * its majoritaty prediction.
     */
    private class Pair implements Comparable<Pair>{
        public int index;
        public double value;

        Pair(int index, double value){
            this.index = index;
            this.value = value;
        }

        @Override
        public int compareTo(Pair o) {
            int result = (o.value>this.value)?1:-1;
            return result;
        }
    };

    public CardinalityBaseline() {
    }

    protected void buildInternal(MultiLabelInstances train) throws Exception {
        output = getMajoritaryPredictions(train);
    }


    protected MultiLabelOutput makePredictionInternal(Instance instance) {
        return new MultiLabelOutput(output);
    }


    private boolean[] getMajoritaryPredictions(MultiLabelInstances dataSet){

        HashMap<String, double[]> predictions = new HashMap<String,double[]>();

        int[] labelIndices = dataSet.getLabelIndices();
        int numInstances = dataSet.getNumInstances();

        int numLabels = dataSet.getNumLabels();
        boolean[] trueLabels = new boolean[numLabels];

		/* Frequency counter for each label */
        double [] labelFrequency = new double [numLabels];
        for(int i=0;i<numLabels;i++)
            labelFrequency[i] = 0.0;

		/* Create the sets */
        boolean[] hammingLossPrediction = new boolean[numLabels];
        ///	boolean[] subsetAccuracyPrediction = new boolean[numLabels];
        boolean[] sizeBasedPrediction = new boolean[numLabels];

        boolean[] finalHamming = new boolean[numLabels];
        boolean[] finalCard  = new boolean[numLabels];
        boolean[] finalFM = new boolean[numLabels];
        boolean[] finalAcc = new boolean[numLabels];
        boolean[] finalPrc = new boolean[numLabels];
        boolean[] finalSA = new boolean[numLabels];


		/* Inicialize the measures */
        double [] hammingLoss = new double[6];
        double [] subsetAccuracy = new double[6];
        double [] fmeasure = new double[6];
        double [] precision = new double[6];
        double [] accuracy = new double[6];
        double [] rankPrediction = new double[6];

        HashMap<boolean[], Integer> ocurrence = new HashMap<boolean[], Integer>();
        int bestSA = 0;
        int sa_size = 0;

        // Loop through the examples counting the frequencies
        for (int instanceIndex = 0; instanceIndex < numInstances; instanceIndex++) {

            Instance instance = dataSet.getDataSet().instance(instanceIndex);
            if (dataSet.hasMissingLabels(instance)) {
                continue;
            }
            trueLabels = getTrueLabels(instance,numLabels,labelIndices);
            if(ocurrence.get(trueLabels) == null) {
                ocurrence.put(trueLabels, Integer.valueOf(1));
                if(bestSA==0) {
                    sa_size = 0;
                    for(int i = 0; i < numLabels; i++) {
                        finalSA[i] = trueLabels[i];
                        if(trueLabels[i]) sa_size++;
                    }
                    bestSA = 1;
                }
            } else {
                int x = ocurrence.get(trueLabels);
                ocurrence.put(trueLabels, x+1);
                if(x+1 > bestSA) {
                    sa_size = 0;
                    for(int i = 0; i < numLabels; i++) {
                        finalSA[i] = trueLabels[i];
                        if(trueLabels[i]) sa_size++;
                    }
                    bestSA = x+1;
                }
            }
            for(int i=0;i<numLabels;i++)
                if(trueLabels[i]==true) labelFrequency[i] += 1.0;
        }

        // labelFrequency is instaciated with each label frequency
        // It builds the set of HammingLoss predictions
        int hlsize = 0;
        for(int i=0;i<numLabels;i++){
            if(labelFrequency[i]>numInstances/2){
                hammingLossPrediction[i] = true;
                hlsize++;
            }else hammingLossPrediction[i] = false;
        }

        System.arraycopy(hammingLossPrediction,0,finalHamming,0,numLabels);

        fillMeasures(dataSet, hammingLoss, hammingLossPrediction, numLabels, labelIndices, numInstances,trueLabels,hlsize);
        fillMeasures(dataSet, subsetAccuracy, finalSA, numLabels, labelIndices, numInstances, trueLabels, sa_size);

		/* Build the F-Measure, Precision, Accuracy datasets */
        Pair[] orderedLabels = new Pair[numLabels];

        for(int i=0;i<numLabels;i++) orderedLabels[i] = new Pair(i, labelFrequency[i]);
        Arrays.sort(orderedLabels);

        double currentFmeasure = 0.0;
        double currentAccuracy = 0.0;
        double currentPrecision = 0.0;

        for(int size=0;size<=numLabels;size++){
            for(int k=0;k<size;k++){
                sizeBasedPrediction[orderedLabels[k].index] = true;
            }
            for(int k=size;k<numLabels;k++){
                sizeBasedPrediction[orderedLabels[k].index] = false;
            }

            double [] curMeasure = new double[5];
            fillMeasures(dataSet, curMeasure, sizeBasedPrediction, numLabels, labelIndices, numInstances, trueLabels, size);
            if(curMeasure[1]>currentAccuracy){
                currentAccuracy = curMeasure[1];
                System.arraycopy(curMeasure, 0, accuracy, 0, 5);
                System.arraycopy(sizeBasedPrediction,0,finalAcc,0,numLabels);
            }
            if(curMeasure[2]>currentFmeasure){
                currentFmeasure = curMeasure[2];
                System.arraycopy(curMeasure, 0, fmeasure, 0, 5);
                System.arraycopy(sizeBasedPrediction, 0, finalFM, 0, numLabels);
            }
            if(curMeasure[3]>currentPrecision){
                currentPrecision = curMeasure[3];
                System.arraycopy(curMeasure, 0, precision, 0, 5);
                System.arraycopy(sizeBasedPrediction, 0, finalPrc, 0, numLabels);
            }
        }

        int totFreq  = 0;
        for(int i=0;i<numLabels;i++)
            totFreq += labelFrequency[i];
        totFreq /= numInstances;
        int size = totFreq;

        for(int k=0;k<size;k++){
            sizeBasedPrediction[orderedLabels[k].index] = true;
        }
        for(int k=size;k<numLabels;k++){
            sizeBasedPrediction[orderedLabels[k].index] = false;
        }

        System.arraycopy(sizeBasedPrediction, 0, finalCard, 0, numLabels);

        fillMeasures(dataSet, rankPrediction, sizeBasedPrediction, numLabels, labelIndices, numInstances, trueLabels, size);

        predictions.put("Hamming Loss", hammingLoss);
        predictions.put("Accuracy",accuracy);
        predictions.put("FMeasure",fmeasure);
        predictions.put("Precision", precision);
        predictions.put("RankBased",rankPrediction);
        predictions.put("SubsetAccuracy", subsetAccuracy);

        boolean [] used_labels = new boolean[numLabels];
        for(int i=0;i<numLabels;i++) used_labels[i] = false;

        for(int i=0;i<numLabels;i++){
            used_labels[i] =
                    (finalHamming[i] | finalSA[i] | finalCard[i] | finalAcc[i] | finalFM[i] | finalPrc[i]);
        }

        return finalCard;
    }

    /*
	 *  Copy from Evaluator class
	 */
    private boolean[] getTrueLabels(Instance instance, int numLabels, int[] labelIndices) {

        boolean[] trueLabels = new boolean[numLabels];
        for (int counter = 0; counter < numLabels; counter++) {
            int classIdx = labelIndices[counter];
            String classValue = instance.attribute(classIdx).value((int) instance.value(classIdx));
            trueLabels[counter] = classValue.equals("1");
        }

        return trueLabels;
    }


    private void fillMeasures(MultiLabelInstances dataSet, double [] measureSet, boolean [] predictionSet, int numLabels, int [] labelIndices,
                              int numInstances, boolean [] trueLabels, int size){

        for(int i=0;i<5;i++) measureSet[i] = 0.0;

        for (int instanceIndex = 0; instanceIndex < numInstances; instanceIndex++) {
            Instance instance = dataSet.getDataSet().instance(instanceIndex);
            if (dataSet.hasMissingLabels(instance)) {
                continue;
            }
            trueLabels = getTrueLabels(instance,numLabels,labelIndices);

            double trueLabelSize = 0.0;
            double hl_factor = 0.0;
            double fm_factor = 0.0;
            double prc_factor = 0.0;
            double acc_factor = 0.0;

            int SA = 1;

            for(int i=0;i<numLabels;i++){
                if(trueLabels[i]!=predictionSet[i]) {
                    hl_factor += 1.0;
                    SA = 0;
                }
                if(trueLabels[i]==true && predictionSet[i]==true)
                {
                    prc_factor += 1.0;
                    fm_factor += 1.0;
                    acc_factor += 1.0;
                }
                if(trueLabels[i]) trueLabelSize += 1.0;
            }

            acc_factor /= (size+trueLabelSize-acc_factor);
            fm_factor /= (size+trueLabelSize);
            prc_factor /= (size);
            measureSet[1] += acc_factor;
            measureSet[2] += fm_factor;
            measureSet[3] += prc_factor;
            measureSet[4] += SA;
            measureSet[0] += (hl_factor/numLabels);
        }

        measureSet[0] /= numInstances;
        measureSet[4] /= numInstances;
        measureSet[2] *= (2.0/(double)numInstances);
        measureSet[3] /= (double) numInstances;
        measureSet[1] /= (double) numInstances;
        // HL ACC FM PREC SA
    }

    @Override
    public TechnicalInformation getTechnicalInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    /*@Override
    public String globalInfo() {
        // TODO Auto-generated method stub
        return null;
    }*/
}
