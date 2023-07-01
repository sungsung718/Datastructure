import java.io.*;
import java.util.*;

public class SortingTest
{
    public static void main(String args[])
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try
        {
            boolean isRandom = false;
            int[] value;
            String nums = br.readLine();
            if (nums.charAt(0) == 'r')
            {
                isRandom = true;

                String[] nums_arg = nums.split(" ");

                int numsize = Integer.parseInt(nums_arg[1]);
                int rminimum = Integer.parseInt(nums_arg[2]);
                int rmaximum = Integer.parseInt(nums_arg[3]);

                Random rand = new Random();

                value = new int[numsize];
                for (int i = 0; i < value.length; i++)
                    value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
            }
            else
            {
                int numsize = Integer.parseInt(nums);

                value = new int[numsize];
                for (int i = 0; i < value.length; i++)
                    value[i] = Integer.parseInt(br.readLine());
            }

            while (true)
            {
                int[] newvalue = (int[])value.clone();
                char algo = ' ';

                if (args.length == 4) {
                    return;
                }

                String command = args.length > 0 ? args[0] : br.readLine();

                if (args.length > 0) {
                    args = new String[4];
                }

                long t = System.currentTimeMillis();
                switch (command.charAt(0))
                {
                    case 'B':	// Bubble Sort
                        newvalue = DoBubbleSort(newvalue);
                        break;
                    case 'I':	// Insertion Sort
                        newvalue = DoInsertionSort(newvalue);
                        break;
                    case 'H':	// Heap Sort
                        newvalue = DoHeapSort(newvalue);
                        break;
                    case 'M':	// Merge Sort
                        newvalue = DoMergeSort(newvalue);
                        break;
                    case 'Q':	// Quick Sort
                        newvalue = DoQuickSort(newvalue);
                        break;
                    case 'R':	// Radix Sort
                        newvalue = DoRadixSort(newvalue);
                        break;
                    case 'S':	// Search
                        algo = DoSearch(newvalue);
                        break;
                    case 'X':
                        return;
                    default:
                        throw new IOException("Wrong sorting method");
                }
                if (isRandom)
                {
                    System.out.println((System.currentTimeMillis() - t) + " ms");
                }
                else
                {
                    if (command.charAt(0) != 'S') {
                        for (int i = 0; i < newvalue.length; i++) {
                            System.out.println(newvalue[i]);
                        }
                    } else {
                        System.out.println(algo);
                    }
                }

            }
        }
        catch (IOException e)
        {
            System.out.println("Wrong input: " + e.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private static int[] DoBubbleSort(int[] value)
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
    private static int[] DoInsertionSort(int[] value)
    {
        for (int i=1; i<value.length; i++) {
            int num = value[i];
            int j = i-1;
            while (j >= 0 && num < value[j]) {
                value[j+1] = value[j];
                j--;
            }
            value[j+1] = num;
        }
        return (value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int[] DoHeapSort(int[] value)
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
    private static int[] DoMergeSort(int[] value)
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
    private static int[] DoQuickSort(int[] value)
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
    private static int[] DoRadixSort(int[] value)
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

