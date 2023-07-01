import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Tester {
    public static void main(String args[]) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            int[] testSizes = new int[]{1000, 10000, 50000};
            int iterNum = 5;
            double ratio = 0;

            String command = br.readLine();
            if (command.charAt(0)=='1')
                checkForDifferentDigits(testSizes, iterNum);
            else if (command.charAt(0)=='2')
                checkForDuplicateRatios(testSizes, iterNum);
            else if (command.charAt(0)=='3')
                checkForPartlySorted(testSizes, iterNum);
            else if (command.charAt(0)=='4') {
                ratio = Double.parseDouble(br.readLine());
                checkForSpecificDuplicateRatios(ratio, testSizes, iterNum);
            }
        }

        catch (Exception e)
        {
            System.out.println("Error: " + e.toString());
        }


    }

    private static void checkForSpecificDuplicateRatios(double ratio, int[] testSizes, int iterNum) {
        for (int size: testSizes) {
            System.out.print("Test size: ");
            System.out.print(size);
            System.out.print(", ratio: ");
            System.out.println(ratio);
            long[] bubbleTimes = new long[iterNum];
            long[] insertionTimes = new long[iterNum];
            long[] heapTimes = new long[iterNum];
            long[] mergeTimes = new long[iterNum];
            long[] quickTimes = new long[iterNum];
            long[] radixTimes = new long[iterNum];

            for (int j=0; j<iterNum; j++) {
                int[] value = createDuplicateDataset(ratio, size);
                int[] newValue = value.clone();

                Sorting.DoSearch(newValue);

                long t = System.currentTimeMillis();
                Sorting.DoBubbleSort(newValue);
                bubbleTimes[j] = System.currentTimeMillis()-t;

                newValue = value.clone();
                t = System.currentTimeMillis();
                Sorting.DoInsertionSort(newValue);
                insertionTimes[j] = System.currentTimeMillis()-t;

                newValue = value.clone();
                t = System.currentTimeMillis();
                Sorting.DoHeapSort(newValue);
                heapTimes[j] = System.currentTimeMillis()-t;

                newValue = value.clone();
                t = System.currentTimeMillis();
                Sorting.DoMergeSort(newValue);
                mergeTimes[j] = System.currentTimeMillis()-t;

                if (!(size==50000 && ratio==1)) {
                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoQuickSort(newValue);
                    quickTimes[j] = System.currentTimeMillis()-t;
                }

                newValue = value.clone();
                t = System.currentTimeMillis();
                Sorting.DoRadixSort(newValue);
                radixTimes[j] = System.currentTimeMillis()-t;


            }

            double[] bubbleStats = calculateMeanAndSD(bubbleTimes);
            double[] insertionStats = calculateMeanAndSD(insertionTimes);
            double[] heapStats = calculateMeanAndSD(heapTimes);
            double[] mergeStats = calculateMeanAndSD(mergeTimes);
            double[] quickStats = calculateMeanAndSD(quickTimes);
            double[] radixStats = calculateMeanAndSD(radixTimes);

            System.out.print("bubble: ");
            System.out.print(bubbleStats[0]);
            System.out.print(" ");
            System.out.println(bubbleStats[1]);

            System.out.print("insertion: ");
            System.out.print(insertionStats[0]);
            System.out.print(" ");
            System.out.println(insertionStats[1]);

            System.out.print("heap: ");
            System.out.print(heapStats[0]);
            System.out.print(" ");
            System.out.println(heapStats[1]);

            System.out.print("merge: ");
            System.out.print(mergeStats[0]);
            System.out.print(" ");
            System.out.println(mergeStats[1]);

            System.out.print("quick: ");
            if (size==50000 && ratio==1) {
                System.out.println("NA");
            } else {
                System.out.print(quickStats[0]);
                System.out.print(" ");
                System.out.println(quickStats[1]);
            }


            System.out.print("radix: ");
            System.out.print(radixStats[0]);
            System.out.print(" ");
            System.out.println(radixStats[1]);
            System.out.println("-----------------------------");
        }
    }

    private static void checkForPartlySorted(int[] testSizes, int iterNum) {
        for (int size: testSizes) {
            for (int i=1; i<=10; i++) {
                double ratio = 0.1*i;
                System.out.print("Test size: ");
                System.out.print(size);
                System.out.print(", ratio: ");
                System.out.println(ratio);
                long[] bubbleTimes = new long[iterNum];
                long[] insertionTimes = new long[iterNum];
                long[] heapTimes = new long[iterNum];
                long[] mergeTimes = new long[iterNum];
                long[] quickTimes = new long[iterNum];
                long[] radixTimes = new long[iterNum];

                for (int j=0; j<iterNum; j++) {
                    int[] value = createPartlySortedDataset(ratio, size);
                    int[] newValue = value.clone();

                    Sorting.DoSearch(newValue);

                    long t = System.currentTimeMillis();
                    Sorting.DoBubbleSort(newValue);
                    bubbleTimes[j] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoInsertionSort(newValue);
                    insertionTimes[j] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoHeapSort(newValue);
                    heapTimes[j] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoMergeSort(newValue);
                    mergeTimes[j] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    if (size != 50000) {
                        t = System.currentTimeMillis();
                        Sorting.DoQuickSort(newValue);
                        quickTimes[j] = System.currentTimeMillis() - t;
                    }

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoRadixSort(newValue);
                    radixTimes[j] = System.currentTimeMillis()-t;
                }

                double[] bubbleStats = calculateMeanAndSD(bubbleTimes);
                double[] insertionStats = calculateMeanAndSD(insertionTimes);
                double[] heapStats = calculateMeanAndSD(heapTimes);
                double[] mergeStats = calculateMeanAndSD(mergeTimes);
                double[] quickStats = calculateMeanAndSD(quickTimes);
                double[] radixStats = calculateMeanAndSD(radixTimes);

                System.out.print("bubble: ");
                System.out.print(bubbleStats[0]);
                System.out.print(" ");
                System.out.println(bubbleStats[1]);

                System.out.print("insertion: ");
                System.out.print(insertionStats[0]);
                System.out.print(" ");
                System.out.println(insertionStats[1]);

                System.out.print("heap: ");
                System.out.print(heapStats[0]);
                System.out.print(" ");
                System.out.println(heapStats[1]);

                System.out.print("merge: ");
                System.out.print(mergeStats[0]);
                System.out.print(" ");
                System.out.println(mergeStats[1]);

                System.out.print("quick: ");
                if (size == 50000) {
                    System.out.println("NA");
                } else {
                    System.out.print(quickStats[0]);
                    System.out.print(" ");
                    System.out.println(quickStats[1]);
                }

                System.out.print("radix: ");
                System.out.print(radixStats[0]);
                System.out.print(" ");
                System.out.println(radixStats[1]);
                System.out.println("-----------------------------");
            }
        }
    }

    private static void checkForDuplicateRatios(int[] testSizes, int iterNum) {
        for (int size: testSizes) {
            for (int i=1; i<=10; i++) {
                double ratio = 0.1*i;
                System.out.print("Test size: ");
                System.out.print(size);
                System.out.print(", ratio: ");
                System.out.println(ratio);
                long[] bubbleTimes = new long[iterNum];
                long[] insertionTimes = new long[iterNum];
                long[] heapTimes = new long[iterNum];
                long[] mergeTimes = new long[iterNum];
                long[] quickTimes = new long[iterNum];
                long[] radixTimes = new long[iterNum];

                for (int j=0; j<iterNum; j++) {
                    int[] value = createDuplicateDataset(ratio, size);
                    int[] newValue = value.clone();

                    Sorting.DoSearch(newValue);

                    long t = System.currentTimeMillis();
                    Sorting.DoBubbleSort(newValue);
                    bubbleTimes[j] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoInsertionSort(newValue);
                    insertionTimes[j] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoHeapSort(newValue);
                    heapTimes[j] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoMergeSort(newValue);
                    mergeTimes[j] = System.currentTimeMillis()-t;

                    if (!(size==50000 && i==10)) {
                        newValue = value.clone();
                        t = System.currentTimeMillis();
                        Sorting.DoQuickSort(newValue);
                        radixTimes[j] = System.currentTimeMillis()-t;
                    }

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoRadixSort(newValue);
                    quickTimes[j] = System.currentTimeMillis()-t;
                }

                double[] bubbleStats = calculateMeanAndSD(bubbleTimes);
                double[] insertionStats = calculateMeanAndSD(insertionTimes);
                double[] heapStats = calculateMeanAndSD(heapTimes);
                double[] mergeStats = calculateMeanAndSD(mergeTimes);
                double[] quickStats = calculateMeanAndSD(quickTimes);
                double[] radixStats = calculateMeanAndSD(radixTimes);

                System.out.print("bubble: ");
                System.out.print(bubbleStats[0]);
                System.out.print(" ");
                System.out.println(bubbleStats[1]);

                System.out.print("insertion: ");
                System.out.print(insertionStats[0]);
                System.out.print(" ");
                System.out.println(insertionStats[1]);

                System.out.print("heap: ");
                System.out.print(heapStats[0]);
                System.out.print(" ");
                System.out.println(heapStats[1]);

                System.out.print("merge: ");
                System.out.print(mergeStats[0]);
                System.out.print(" ");
                System.out.println(mergeStats[1]);

                System.out.print("quick: ");
                if (size==50000 && i==10) {
                    System.out.println("NA");
                } else {
                    System.out.print(quickStats[0]);
                    System.out.print(" ");
                    System.out.println(quickStats[1]);
                }


                System.out.print("radix: ");
                System.out.print(radixStats[0]);
                System.out.print(" ");
                System.out.println(radixStats[1]);
                System.out.println("-----------------------------");
            }
        }
    }

    private static void checkForDifferentDigits(int[] testSizes, int iterNum) {
        for (int size: testSizes) {
            for (int digit=1; digit<10; digit++) {
                System.out.print("Test size: ");
                System.out.print(size);
                System.out.print(", digit: ");
                System.out.println(digit);
                long[] bubbleTimes = new long[iterNum];
                long[] insertionTimes = new long[iterNum];
                long[] heapTimes = new long[iterNum];
                long[] mergeTimes = new long[iterNum];
                long[] quickTimes = new long[iterNum];
                long[] radixTimes = new long[iterNum];

                for (int i=0; i<iterNum; i++) {
                    int[] value = createMaxNumDigitDataset(digit, size);
                    int[] newValue = value.clone();

                    Sorting.DoSearch(newValue);

                    long t = System.currentTimeMillis();
                    Sorting.DoBubbleSort(newValue);
                    bubbleTimes[i] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoInsertionSort(newValue);
                    insertionTimes[i] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoHeapSort(newValue);
                    heapTimes[i] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoMergeSort(newValue);
                    mergeTimes[i] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoQuickSort(newValue);
                    quickTimes[i] = System.currentTimeMillis()-t;

                    newValue = value.clone();
                    t = System.currentTimeMillis();
                    Sorting.DoRadixSort(newValue);
                    radixTimes[i] = System.currentTimeMillis()-t;
                }

                double[] bubbleStats = calculateMeanAndSD(bubbleTimes);
                double[] insertionStats = calculateMeanAndSD(insertionTimes);
                double[] heapStats = calculateMeanAndSD(heapTimes);
                double[] mergeStats = calculateMeanAndSD(mergeTimes);
                double[] quickStats = calculateMeanAndSD(quickTimes);
                double[] radixStats = calculateMeanAndSD(radixTimes);

                System.out.print("bubble: ");
                System.out.print(bubbleStats[0]);
                System.out.print(" ");
                System.out.println(bubbleStats[1]);

                System.out.print("insertion: ");
                System.out.print(insertionStats[0]);
                System.out.print(" ");
                System.out.println(insertionStats[1]);

                System.out.print("heap: ");
                System.out.print(heapStats[0]);
                System.out.print(" ");
                System.out.println(heapStats[1]);

                System.out.print("merge: ");
                System.out.print(mergeStats[0]);
                System.out.print(" ");
                System.out.println(mergeStats[1]);

                System.out.print("quick: ");
                System.out.print(quickStats[0]);
                System.out.print(" ");
                System.out.println(quickStats[1]);

                System.out.print("radix: ");
                System.out.print(radixStats[0]);
                System.out.print(" ");
                System.out.println(radixStats[1]);
                System.out.println("-----------------------------");
            }
        }
    }

    private static int[] createMaxNumDigitDataset(int digit, int size) {
        int max = (int) Math.pow(10, digit) + 1;
        int min = -1*max;

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        int[] value = new int[size];
        for (int i = 0; i < value.length; i++)
            value[i] = rand.nextInt(max - min + 1) + min;

        return value;
    }

    private static int[] createDuplicateDataset(double ratio, int size) {
        int[] value = new int[size];
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        if (ratio == 1) {
            int num = rand.nextInt();
            Arrays.fill(value, num);
        } else {
            int[] tmp = new int[(int) ((1-ratio) * size)];
            for (int i = 0; i < tmp.length; i++) {
                tmp[i] = rand.nextInt();
            }

            for (int i = 0; i < value.length; i++) {
                value[i] = tmp[i % tmp.length];
            }
        }
        return value;
    }

    private static int[] createPartlySortedDataset(double ratio, int size) {
        int[] value = new int[size];
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        value[0] = rand.nextInt();
        for (int i=0; i<size-1; i++) {
            if (rand.nextDouble()<ratio) {
                value[i+1] = value[i] + rand.nextInt(100000);
            } else {
                value[i+1] = value[i] - rand.nextInt(100000);
            }
        }

        return value;
    }

    private static double[] calculateMeanAndSD(long arr[])
    {
        double sum = 0.0;
        double mean = 0.0;
        double sd = 0.0;
        double[] result = new double[2];
        int length = arr.length;

        for (double num : arr) {
            sum += num;
        }
        mean = sum/length;

        for (double num: arr) {
            sd += Math.pow(num - mean, 2);
        }
        sd = Math.sqrt(sd/length);

        result[0] = mean;
        result[1] = sd;

        return result;
    }
}

class Sorting {

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int[] DoBubbleSort(int[] value)
    {
        boolean swapped;

        for (int last=value.length-1; last>=1; last--) {
            swapped = false;
            for (int i=0; i<last; i++) {
                if (value[i] > value[i+1]) {
                    int tmp = value[i];
                    value[i] = value[i+1];
                    value[i+1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return (value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int[] DoInsertionSort(int[] value)
    {
        for (int i=1; i<value.length; i++) {
            int newItem = value[i];
            int j = i-1;
            while (j >= 0 && newItem < value[j]) {
                value[j+1] = value[j];
                j--;
            }
            value[j+1] = newItem;
        }
        return (value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int[] DoHeapSort(int[] value)
    {
        buildHeap(value);
        int tmp;
        for (int i=value.length-1; i>0; i--) {
            tmp = value[0];
            value[0] = value[i];
            value[i] = tmp;
            percolateDown(0, i-1, value);
        }
        return (value);
    }

    private static void buildHeap(int[] value) {
        if (value.length >= 2) {
            for (int i=(value.length-2)/2; i>=0; i--) {
                percolateDown(i, value.length-1, value);
            }
        }
    }

    private static void percolateDown(int i, int n, int[] value) {
        int child = 2*i+1;
        int rightChild = 2*i+2;
        if (child <= n) {
            if ((rightChild <= n) && (value[child]) < value[rightChild]) {
                child = rightChild;
            }
            if (value[i] < value[child]) {
                int tmp = value[i];
                value[i] = value[child];
                value[child] = tmp;
                percolateDown(child, n, value);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int[] DoMergeSort(int[] value)
    {
        int[] tmp = new int[value.length];
        for (int i=0; i<value.length; i++) {
            tmp[i] = value[i];
        }
        mergeSort(0, value.length-1, value, tmp);
        return (value);
    }

    private static void mergeSort(int first, int last, int[] value, int[] tmp) {
        if (first < last) {
            int mid = (first + last) / 2;
            mergeSort(first, mid, tmp, value);
            mergeSort(mid + 1, last, tmp, value);
            merge(first, mid, last, tmp, value);
        }
    }

    private static void merge(int first, int mid, int last, int[] src, int[] dest) {
        int i = first;
        int j = mid+1;
        int k = first;

        while (i <= mid && j <= last) {
            if (src[i] < src[j])
                dest[k++] = src[i++];
            else
                dest[k++] = src[j++];
        }

        while (i <= mid)
            dest[k++] = src[i++];
        while (j <= last)
            dest[k++] = src[j++];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int[] DoQuickSort(int[] value)
    {
        quickSort(value, 0, value.length - 1);
        return (value);
    }

    private static void quickSort(int[] value, int first, int last) {
        if (first < last) {
            int mid = partition(value, first, last);
            quickSort(value, first, mid-1);
            quickSort(value, mid+1, last);
        }
    }

    private static int partition(int[] value, int first, int last) {
        int pivot = value[last];

        int i = first-1;

        for (int j = first; j<last; j++) {
            if (value[j] <= pivot) {
                i++;
                swap(value, i, j);
            }
        }
        swap(value, i+1, last);

        return i+1;
    }

    private static void swap(int[] value, int i, int j) {
        int tmp = value[i];
        value[i] = value[j];
        value[j] = tmp;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // ChatGPT
    public static int[] DoRadixSort(int[] value)
    {
        int[] posArr = new int[value.length];
        int[] negArr = new int[value.length];
        int posIndex = 0;
        int negIndex = 0;
        int max = 0;
        int min = 0;

        // Create posArr & negArr
        for (int num: value) {
            if (num >= 0) {
                if (num > max)
                    max = num;
                posArr[posIndex++] = num;
            } else {
                if (num < min)
                    min = num;
                negArr[negIndex++] = num;
            }
        }

        radixSortByAbs(posArr, posIndex, (int)Math.log10(max)+1);
        radixSortByAbs(negArr, negIndex, (int)Math.log10(-1 * min)+1);

        // Merge
        int i = 0;
        for (int j=negIndex-1; j>=0; j--) {
            value[i++] = negArr[j];
        }
        for (int j=0; j < posIndex; j++) {
            value[i++] = posArr[j];
        }

        return (value);
    }

    private static void radixSortByAbs(int[] arr, int len, int numDigits) {
        if (len > 0) {
            for (int digit = 1; digit <= numDigits; digit++) {
                countingSortByAbs(arr, len, digit);
            }
        }
    }

    private static void countingSortByAbs(int[] arr, int len, int digit) {
        int[] cnt = new int[10];
        int[] start = new int[10];
        int[] output = new int[len];
        int placeValue;

        for (int i=0; i<len; i++) {
            placeValue = (int) (Math.abs(arr[i]) / Math.pow(10, digit - 1) % 10);
            cnt[placeValue]++;
        }
        for (int d=1; d<10; d++) {
            start[d] = start[d-1] + cnt[d-1];
        }
        for (int i=0; i<len; i++) {
            placeValue= (int) (Math.abs(arr[i]) / Math.pow(10, digit - 1) % 10);
            output[start[placeValue]++] = arr[i];
        }
        for (int i=0; i<len; i++) {
            arr[i] = output[i];
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static char DoSearch(int[] value)
    {
        int maxNumDigits = 1;
        int maxAbs = 0;

        float duplicateRatio = 0;
        int numDuplicates = 0;
        HashSet<Integer> hashTable = new HashSet<>(); // import java.util.Hashset

        float sortedRatio = 0;
        int numSorted = 0;

        for (int i=0; i<value.length-1; i++) {
            int curr = value[i];
            int next = value[i+1];

            // update MaxAbs
            maxAbs = Math.max(Math.abs(curr), maxAbs);

            // update numDuplicates & hash table
            if (hashTable.contains(curr))
                numDuplicates++;
            else
                hashTable.add(curr);

            // update numSorted
            if (curr <= next)
                numSorted++;
        }

        int lastNum = value[value.length-1];
        maxAbs = Math.max(Math.abs(lastNum), maxAbs);
        if (hashTable.contains(lastNum))
            numDuplicates++;
        else
            hashTable.add(lastNum);


        maxNumDigits = (int)Math.log10(maxAbs)+1;
        duplicateRatio = (float) numDuplicates / value.length;
        sortedRatio = (float) numSorted / value.length;

        if (sortedRatio == 1) {
            return 'B';
        } else if (Math.abs(sortedRatio - 0.5) > 0.01 || duplicateRatio > 0.98) {
            return 'M';
        } else {
            return 'Q';
        }
    }
}
}
