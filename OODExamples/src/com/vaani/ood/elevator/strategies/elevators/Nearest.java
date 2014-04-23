package com.vaani.ood.elevator.strategies.elevators;

import java.util.ArrayList;
import com.vaani.ood.elevator.main.Console;
import com.vaani.ood.elevator.models.Elevator;
import com.vaani.ood.elevator.models.Passenger;
import com.vaani.ood.elevator.strategies.ElevatorStrategy;

public class Nearest extends ElevatorStrategy {

	public Nearest() {
		super();
	}

	public Nearest(Elevator elevator) {
		super(elevator);
	}

	@Override
	public String getName() {
		return "Nearest : Comportement qui choisit l'�tage (ou il y a quelque chose � faire) le plus proche de lui.";
	}

	@Override
	public Class getType() {
		return ElevatorStrategy.class;
	}

	@Override
	public void acts() {
		// Aucun appel, on ne fait rien, on est a l'arret, tout va bien
		if(!elevator.getBuilding().allPassengersAreArrived() && (elevator.getBuilding().getWaitingPersonsCount() != 0 || elevator.getPassengerCount() != 0)) {

			// Si on n'a pas encore d'�tage vis� ou que l'on est arriv� a l'�tage vis�
			// on s'arrete et on commence le travail
			if((elevator.getTargetFloor() == Integer.MAX_VALUE) || (elevator.getCurrentFloor() == elevator.getTargetFloor())) {
				elevator.setMoving(false);

				// Ceux qui veulent descendre desendent
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

				// On r�cupre la liste des etages demand�s par les passagers de l'ascenseur
				ArrayList<Integer> tabWanted = elevator.getFloorsWanted();

				// Si l'ascenseur n'est pas plein ou en alerte, on fusionne cette liste
				// avec la liste des �tages ou des passagers attendent
				if(!elevator.isFull() && !elevator.isInAlert()) {
					ArrayList<Integer> tabWaiting = elevator.getBuilding().getFloorWithWaitingPassengers();
					// Merge des deux listes
					for (Integer i : tabWaiting) {
						if(!tabWanted.contains(i)){
							tabWanted.add(i);
						}
					}
				}

				// On s�lectionne un �tage au hasard et on change le targetFloor de l'ascenseur
				int target_floor;
				if(!tabWanted.isEmpty()) {
					int nearest_floor = elevator.atTop() ? elevator.getCurrentFloor()-1 : (elevator.atBottom() ? elevator.getCurrentFloor()+1 : elevator.getCurrentFloor()+1);
					int min_distance = Integer.MAX_VALUE;
					for (Integer floor : tabWanted) {
						if(Math.sqrt((elevator.getCurrentFloor()-floor)*(elevator.getCurrentFloor()-floor)) < min_distance) {
							min_distance = (int) Math.sqrt( (elevator.getCurrentFloor()-floor)*(elevator.getCurrentFloor()-floor) );
							nearest_floor = floor;
						}
					}
					target_floor = nearest_floor;
					elevator.setTargetFloor(target_floor);
				}
			}

			elevator.incrementStoppedTime(1);
			if((elevator.getStoppedTime() >= elevator.getStopTime())) elevator.leaveThisFloor();
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
