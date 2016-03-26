//CSc206 - Final Question 1
//Genetic Algorithms
//Bhargi Patel

import java.util.*;

public class GeneticAlgo 
{
	int populationSize;
	double crossoverRate;
	double mutationRate;
	int generationSize;
	int[] individuals;
	double[] Fx;
	double[] cumulativeNormedFx;
	int best;
	int x = 2147483647;
	int digits = 31;
	
	//Convert Decimal to Binary
	public int[] convertToBinary(int num,int digits) 
	{
		int i = 0, temp[] = new int[digits];
		int binary[];
		while (num > 0) 
		{
			temp[i++] = num % 2;
			num /= 2;
		}
		binary = new int[digits];
		int l=0;
		//fill values with zero so that we always have uniform digits
		for(int k = 0 ; k< digits-i; k++)
			binary[k] = 0;
		for (int j = i - 1; j >= 0; j--) 
			binary[l++] = temp[j];
		return binary;
	}
	
	// Convert Binary to Decimal
	private  int Bin2Dec(int[] bin) 
	{
		int decValue=0;
		for (int i=0; i<bin.length; i++)
		{
			decValue <<= 1; 		// Shift content of decValue 1 position to the left
			decValue += bin[i]; 	// add the 0 or 1
		}
		return decValue;	
	}
	
	//Calculate f(x) = sin(x*Pi/2147483648)
	public double calculateFx(int num)
	{
		double value = (num * Math.PI)/2147483648L;
		return Math.sin(value);
	}
	
	//Calculate the best value of f(x) from current generation
	public void calculateBest()
	{
		double temp = Fx[0];
		int index = 0;
		for(int i = 1; i<populationSize;i++)
		{
			if(temp < Fx[i])
			{
				temp = Fx[i];
				index = i;
			}
		}
		System.out.println("The Best x is " + individuals[index] + ", F(" + individuals[index] + ") = " + String.format("%.6f", temp ));
	}
	
	//Calculate the normed f(x) and cumulative normed f(x)
	public void calculateCumulativeNormedFx()
	{
		Fx = new double[populationSize];
		double[] normedFx = new double[populationSize];
		cumulativeNormedFx = new double[populationSize];
		double sumFx = 0.0;
		
		//calculate f(x)
		for(int i = 0; i < populationSize;i++)
			Fx[i] = calculateFx(individuals[i]);
		
		//generate normed fx
		for(int i = 0; i< populationSize;i++)
			sumFx += Fx[i];

		for(int i = 0; i< populationSize;i++)
			normedFx[i] = Fx[i]/sumFx;
		
		//generate cumulative normed fx
		for(int i = 0; i < populationSize; i++)
		{
			double temp = 0.0;
			for(int j = i; j >=0; j--)
				temp += normedFx[j];
			cumulativeNormedFx[i] = temp;
		}
	}
	
	//Initialize population -  First Generation
	public void initializePopulation()
	{
		Random randomGenerator = new Random();
		individuals = new int[populationSize];
		
		//creating individuals in the range 0 <= x <= 2147483647
		for(int i = 0; i< populationSize; i++)
			individuals[i] = randomGenerator.nextInt(x);
		calculateCumulativeNormedFx();
	}
	
	//perform crossOver
	public void crossOver(int a, int b, int index)
	{
		//call random number generator for one point crossover
		Random randomGenerator = new Random();				
		int start = randomGenerator.nextInt(digits-1);	
		//get the binary value of a
		int[] ind1 = convertToBinary(a,digits);
		int[] ind2 = convertToBinary(b,digits);
		int[] tmpInd1 = new int[digits];
		
		for(int i = 0; i<digits;i++)
			tmpInd1[i] = ind1[i];
		
		//perform one-point crossover
		for(int i = start ; i< digits;i++)
			ind1[i] = ind2[i];

		for(int i = start; i < digits; i++)
			ind2[i] = tmpInd1[i];
		
		//get the decimal value and replace the array
		individuals[index] = Bin2Dec(ind1);
		individuals[index+1] = Bin2Dec(ind2);
	}
	
	//Perform Mutation
	public void mutation(int a, int index)
	{
		//call random number generator for getting which bit to flip
		Random randomGenerator = new Random();				
		int point = randomGenerator.nextInt(digits-1);
		//get the binary value of a
		int[] ind1 = convertToBinary(a,digits);
		ind1[point] = ind1[point] ==0 ? 1:0;
		//get the decimal value and replace the array
		individuals[index] = Bin2Dec(ind1);
	}
	
	//1. Select individuals for reproduction
	//2. Perform crossover and mutation
	//3. Calculate cumulative normed fx
	public void nextGeneration()
	{
		//randomly select individuals from first generation
		Random randomGenerator = new Random();
		double[] ChosenCumulative = new double[populationSize];
		int[] nextGenerationIndividuals = new int[populationSize];
		for(int i = 0; i< populationSize; i++)
			ChosenCumulative[i] = randomGenerator.nextDouble();

		//get the individuals from the chosen random cumulative no's
		for(int i=0;i<populationSize;i++)
		{
			double temp = 0.0;
			int index = 0;
			for(int j = 0; j< populationSize; j++)
			{
				if(ChosenCumulative[i] > cumulativeNormedFx[j])
					continue;
				//get the smallest absolute value
				if(temp == 0.0)
				{
					temp = cumulativeNormedFx[j]-ChosenCumulative[i];
					index = j;
				}
				else if(temp > cumulativeNormedFx[j]-ChosenCumulative[i])
				{
					temp = cumulativeNormedFx[j]-ChosenCumulative[i];
					index = j;
				}
			}
			nextGenerationIndividuals[i] = individuals[index];
		}
		
		//call random number generator for finding individuals and performing crossover and mutation
		int count = 0;
		while(count < populationSize)
		{
			int individual_one = 0;
			int individual_two = 0;
			Boolean crossOver = false;
			individual_one = nextGenerationIndividuals[randomGenerator.nextInt(populationSize-1)];
			count++;
			if(count != populationSize)	
				individual_two = nextGenerationIndividuals[randomGenerator.nextInt(populationSize-1)];
			
			//call random number generator for crossover on the pair
			if(individual_two != 0)
			{
				double cross = randomGenerator.nextDouble();
				if(cross <= crossoverRate)
				{
					crossOver(individual_one,individual_two,count-1);
					crossOver = true;
				}
			}
			
			//call random number generator for mutation on each
			double mut_one = randomGenerator.nextDouble();
			//use the already performed crossover first
			if(mut_one <= mutationRate)
				mutation(crossOver ? individuals[count-1] : individual_one,count-1);

			if(individual_two != 0)
			{
				double mut_two = randomGenerator.nextDouble();
				//use the already performed crossover first
				if(mut_two <= mutationRate)
					mutation(crossOver ? individuals[count] : individual_two,count);
			}
		}
		calculateCumulativeNormedFx();
	}
	
	public static void main(String[] args) 
	{
		GeneticAlgo ga = new GeneticAlgo();
			//Get population size from user
		System.out.print("Population Size:");
		Scanner in = new Scanner(System.in);
		ga.populationSize = in.nextInt();
			//Get crossover rate
		System.out.print("Crossover Rate:");
		ga.crossoverRate = in.nextDouble();
			//Get mutation rate
		System.out.print("Mutation Rate:");
		ga.mutationRate = in.nextDouble();
			//Get generation size
		System.out.print("Stop after generations:");
		ga.generationSize = in.nextInt();
		in.close();
			//Call Initialize Population
		ga.initializePopulation();
			//Next Generations
		while(ga.generationSize > 1)
		{
			ga.nextGeneration();
			ga.generationSize--;
		}
			//calculate the best f(x) value from current generation
		ga.calculateBest();		
	}
}