package com.vaani.ood.elevator.strategies.elevators;

import com.vaani.ood.elevator.main.Console;
import com.vaani.ood.elevator.models.Elevator;
import com.vaani.ood.elevator.models.Passenger;
import com.vaani.ood.elevator.strategies.ElevatorStrategy;

/**
 *
 * @author francois
 *
 */
public class Linear extends ElevatorStrategy {

	public Linear() {
		super();
	}

	public Linear(Elevator elevator) {
		super(elevator);
	}

	@Override
	public String getName() {
		return "Linear : Comportement lineaire sans optimisation des durees d'attente.";
	}

	@Override
	public Class getType() {
		return ElevatorStrategy.class;
	}

	@Override
	public void acts() {
		// Aucun appel, on ne fait rien, on est a l'arret, tout va bien
		if(!elevator.getBuilding().allPassengersAreArrived() && (elevator.getBuilding().getWaitingPersonsCount() != 0 || elevator.getPassengerCount() != 0)) {
			elevator.setMoving(false);

			elevator.releaseAllArrivedPassengers();

			// Si l'ascenseur n'est pas plein et qu'il ne doit pas partir de suite...
			if(!elevator.isFull() && !must_leave_now) {
				int i = 0;
				// Tant que l'ascenseur n'est pas plein ou en alerte et qu'on a pas test� tous
				// les passagers qui attendent � l'�tage, on entre dans la boucle
				while(!elevator.isFull() && !elevator.isInAlert() && i < elevator.getBuilding().getWaitingPersonsCountAtFloor(elevator.getCurrentFloor())) {
					// On r�cupre le ime passager qui attend � l'�tage pour lui permettre
					// d'essayer de rentrer (si diff�rent de null)
					Passenger p = elevator.getBuilding().getWaitingPassengerAtFloorWithIndex(elevator.getCurrentFloor(), i);
					if(p != null) p.canEnterElevator(elevator);
					i++;
				}
			}

			elevator.incrementStoppedTime(1);

			if((elevator.isGoingToTop() && elevator.atTop()) || (!elevator.isGoingToTop() && elevator.atBottom()) || (elevator.noCallOnTheWay())) {
				elevator.changeDirection(); // Changement de sens pour le prochain mouvement
			}

			if(elevator.getStoppedTime() >= elevator.getStopTime()) elevator.leaveThisFloor();
		}
		else {
			elevator.setMoving(false);
		}
	}

	@Override
	public boolean takePassenger(Passenger passenger) {
		elevator.incrementStopTime(5*passenger.getPersonCount());
		return true;
	}

	@Override
	public void releasePassenger(Passenger passenger) {
		elevator.incrementStopTime(5*passenger.getPersonCount());
		Console.debug("Un passager descend. "+passenger.getCurrentFloor()+" -> "+passenger.getWantedFloor()+" "+passenger.isInTheElevator()+" "+passenger.isArrived());
	}

	@Override
	public void leaveThisFloor() {
	}

}
