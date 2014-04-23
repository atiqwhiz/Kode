package com.vaani.ood.elevator.controllers;

import java.util.ArrayList;
import java.util.LinkedList;



import java.util.Random;

import com.vaani.ood.elevator.factories.SimulatorFactory;
import com.vaani.ood.elevator.main.Console;
import com.vaani.ood.elevator.models.Building;
import com.vaani.ood.elevator.models.Elevator;
import com.vaani.ood.elevator.models.Group;
import com.vaani.ood.elevator.models.Passenger;
import com.vaani.ood.elevator.models.Person;
import com.vaani.ood.elevator.strategies.ElevatorStrategy;
import com.vaani.ood.elevator.views.graphics.AnimatedElevator;
import com.vaani.ood.elevator.views.graphics.AnimatedGroup;
import com.vaani.ood.elevator.views.graphics.AnimatedPerson;
import com.vaani.ood.elevator.views.graphics.FixedFloor;
import com.vaani.ood.elevator.views.graphics.MyFrame;


/**
 *  MainController est construit sur le modele du design pattern Singleton
 *  En effet, il ne peut y avoir qu'une seule instance de ce controleur a la fois.
 *  
 * @author remy
 *
 */
public class MainController {

	// Le point d'acces a tous les modeles (le batiment a acces direct aux elevators et aux passagers)
	private static MainController INSTANCE = null;
	public static Building building = null;

	public Building getBuilding() {
		return building;
	}

	/**
	 * La présence d'un constructeur privé supprime
	 * le constructeur public par défaut.
	 */
	private MainController() {
	}

	/**
	 * Le mot-clé synchronized sur la méthode de création
	 * empêche toute instanciation multiple même par
	 * différents threads.
	 * @return L'unique instance du singleton.
	 */
	public synchronized static MainController getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MainController();
		}
		return INSTANCE;
	}

	public void startSimulation(int floor_count, int elevator_count, int person_per_elevator, int person_count, int group_count, ElevatorStrategy elevator_strategy) throws InstantiationException, IllegalAccessException {
		Console.info("Lancement d'une partie avec " + floor_count + " etages, " + elevator_count +
				" ascenseurs, " + person_per_elevator + " max personnes par ascenseur, " + person_count + " individus et " + group_count + " groupes.");

		SimulatorFactory sf = new SimulatorFactory();

		// Constructs the buildings.
		building = sf.getBuilding(floor_count);

		// Constructs the elevators
		ArrayList<Elevator> elevators = new ArrayList<Elevator>(elevator_count);
		Elevator elevator;
		for (int i = 1; i <= elevator_count; i++) {		
			// Avec plugin
			elevator = sf.getElevator((ElevatorStrategy)elevator_strategy.getClass().newInstance(), person_per_elevator);
			elevator.setIdentifier(i);
			//Placement des Elevators
			Random rand = new Random();
			elevator.setCurrentFloor(rand.nextInt(floor_count+1));
			elevators.add(elevator);
		}
		// Add elevators to the building
		building.setElevators(elevators);

		// Constructs the passengers (only persons for now)
		LinkedList<Passenger> passengers = new LinkedList<Passenger>();
		int j = 0, group_nb = 0;
		while (j < person_count) {
			if (group_nb < group_count) {
				Group group = sf.getGroup(building.getFloorCountWithGround());
				passengers.add(group);
				j += group.getPersonCount();
				group_nb++;
			} else {
				passengers.add(sf.getPerson(building.getFloorCountWithGround()));
				j++;
			}
		}
		// Add passengers to the building
		building.setPassengers(passengers);


		// Graphics!
		MyFrame frame = new MyFrame(elevator_count, building.getFloorCountWithGround());
		
		for (int i = 0; i < elevators.size(); i++) {
			AnimatedElevator e = new AnimatedElevator(elevators.get(i), FixedFloor.FLOOR_WIDTH + (AnimatedElevator.ELEVATOR_WIDTH * i), MyFrame.frame_height - (AnimatedElevator.ELEVATOR_HEIGHT*(elevators.get(i).getCurrentFloor()+1)));
			frame.addAnimatedObject(e);
			elevators.get(i).setAnimatedElevator(e);
		}

		for (int i = 0; i < building.getFloorCountWithGround(); i++) {
			frame.addFixedObject(new FixedFloor(0, MyFrame.frame_height - (AnimatedElevator.ELEVATOR_HEIGHT *(i+1)), i));
			frame.addFixedObject(new FixedFloor(FixedFloor.FLOOR_WIDTH + (elevator_count * AnimatedElevator.ELEVATOR_WIDTH), MyFrame.frame_height - (AnimatedElevator.ELEVATOR_HEIGHT *(i+1)), i));
		}

		Passenger passenger;
		for (int i = 0; i < building.getPassengers().size(); i++) {
			passenger = building.getPassengers().get(i);
			if (passenger instanceof Person) {
				frame.addAnimatedObject(new AnimatedPerson((Person) passenger, FixedFloor.FLOOR_WIDTH - AnimatedPerson.PERSON_WIDTH - (AnimatedPerson.PERSON_WIDTH * building.getPassengerIndexAtHisFloor(passenger)), MyFrame.frame_height - (AnimatedElevator.ELEVATOR_HEIGHT * passenger.getCurrentFloor()) - AnimatedPerson.PERSON_HEIGHT));
			} else if (passenger instanceof Group) {
				frame.addAnimatedObject(new AnimatedGroup((Group) passenger, FixedFloor.FLOOR_WIDTH - AnimatedPerson.PERSON_WIDTH - (AnimatedPerson.PERSON_WIDTH * building.getPassengerIndexAtHisFloor(passenger)), MyFrame.frame_height - (AnimatedElevator.ELEVATOR_HEIGHT * passenger.getCurrentFloor()) - AnimatedPerson.PERSON_HEIGHT));
			}
		}
	}
	
}