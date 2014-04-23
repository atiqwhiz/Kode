package com.vaani.ood.elevator.strategies.elevators;

import com.vaani.ood.elevator.main.Console;
import com.vaani.ood.elevator.models.Elevator;
import com.vaani.ood.elevator.models.Passenger;
import com.vaani.ood.elevator.strategies.ElevatorStrategy;

/**
 *
 * @author remy
 *
 */
public class LinearInTheDirection extends ElevatorStrategy {

	public LinearInTheDirection() {
		super();
	}

	public LinearInTheDirection(Elevator elevator) {
		super(elevator);
	}

	@Override
	public String getName() {
		return "LinearInTheDirection : Comportement lineaire avec duree de voyage minimum (et duree d'attente augmentee)";
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

			if((elevator.isGoingToTop() && elevator.atTop()) || (!elevator.isGoingToTop() && elevator.atBottom())) {
				elevator.changeDirection(); // Changement de sens pour le prochain mouvement
			}

			// Si l'ascenseur n'est pas plein et qu'il ne doit pas partir de suite...
			if(!elevator.isFull() && !must_leave_now) {
				int i = 0;
				// Tant que l'ascenseur n'est pas plein ou en alerte et qu'on a pas test� tous
				// les passagers qui attendent � l'�tage, on entre dans la boucle
				while(!elevator.isFull() && !elevator.isInAlert() && i < elevator.getBuilding().getWaitingPersonsCountAtFloor(elevator.getCurrentFloor())) {
					// On r�cupre le ime passager qui attend � l'�tage pour lui permettre
					// d'essayer de rentrer (si diff�rent de null)
					Passenger p = elevator.getBuilding().getWaitingPassengerAtFloorWithIndexInThisDirection(elevator.getCurrentFloor(), i, elevator.isGoingToTop());
					if(p != null) p.canEnterElevator(elevator);
					i++;
				}
			}

			elevator.incrementStoppedTime(1);
			if(elevator.getStoppedTime() >= elevator.getStopTime()) elevator.leaveThisFloor();

			if(elevator.noCallOnTheWay()) {
				elevator.changeDirection(); // Changement de sens pour le prochain mouvement
			}
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
