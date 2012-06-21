#include <stdio.h>
#include <stdlib.h>

#include <cmuscle.h>

#include "mpiringlib.h"

static void energy_callback(double energy)
{
        static int loop = 0;

        printf("LHC:Energy in loop %d: %f\n", ++loop, energy);
}


int main(int argc, char **argv)
{

	Ring_Init("LHC");

	if (isMasterNode())
	{
		double energy = - 1, deltaEnergy = -1, maxEnergy = -1;
		size_t count = 1;
		int will_stop = 0;

		MUSCLE_Init(&argc, &argv); /* MUSCLE calls are only permitted in the rank 0 process */

		will_stop = MUSCLE_Will_Stop();
		deltaEnergy = atof(MUSCLE_Get_Property("LHC:DeltaEnergy"));
		maxEnergy = atof(MUSCLE_Get_Property("LHC:MaxEnergy"));
		
		while (!will_stop) {
			/* wrapper over MPI_Bcast */
			Ring_Broadcast_Params(&deltaEnergy, &maxEnergy, &will_stop);

			MUSCLE_Receive("pipe-in", &energy, &count, MUSCLE_DOUBLE);

			printf("LHC:Received proton energy: %f\n", energy);

			energy = insertProton(energy, maxEnergy, energy_callback);
			
			will_stop = MUSCLE_Will_Stop();
		}
		Ring_Broadcast_Params(&deltaEnergy, &maxEnergy, &will_stop);

		printf("LHC:Final proton energy: %f\n", energy);

		MUSCLE_Finalize();

	} else
	{
		double deltaEnergy = -1, maxEnergy = -1;
		int will_stop = 0;

		while (1) {
			/* wrapper over MPI_Bcast */
			Ring_Broadcast_Params(&deltaEnergy, &maxEnergy, &will_stop);
			if (will_stop) break;
			
			accelerateProtons(deltaEnergy, maxEnergy);
		}
	}

	Ring_Cleanup();
	return 0;

}
