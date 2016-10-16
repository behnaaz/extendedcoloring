package priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ColoringConnector extends AbstractConnector {
		List<ArrayList<Character>> model;

		public List<ArrayList<Character>> getModel() {
			return model;
		}

		public ColoringConnector(String[] names, List<ArrayList<Character>> model) {
			super(names);
			this.model = model;
		}

		public ColoringConnector(List<String> names, List<ArrayList<Character>> model) {
			super(names);
			this.model =  model;
		}

		ColoringConnector ground(String... boundaries) {
			List<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>();
			for (int i = 0; i < model.size(); i++) {
				boolean copy = true;
				for (int j = 0; j < boundaries.length; j++) {
					if (model.get(i).get(names.indexOf(boundaries[j])) == 'o') {
						copy = false;
						break;
					}
				}
				if (copy)
					result.add(model.get(i));
			}
			return new ColoringConnector(names, result);
		}

		@Override
		void output() {
			for (int i = 0; i < model.size(); i++) {
				if (i == 0) {
					System.out.println("Nodes : "+ model.get(i).size() + " Lines : " + model.size());
					for (int j = 0; j < model.get(i).size(); j++) {
						System.out.print(spaced(names.get(j).length(), Optional.of(names.get(j))));
					}
					System.out.println();
				}
				for (int j = 0; j < model.get(i).size(); j++) {
					System.out.print(spaced(names.get(j).length()+1, Optional.of(model.get(i).get(j).toString())));
				}
				System.out.println();
			}
		}

		boolean isCompatibel(Character c1, Character c2) {
			if (c1 == '.' && c2 == '.')
				return true;
			
			if (c1 == 'x' && c2 == 'x')
				return true;

			
			if (c1 == '.' && c2 == 'o')
				return true;

			if (c1 == 'o' && c2 == '.')
				return true;

			return false;
		}

		void add(ColoringConnector newTable, String newPortName, String existingPortName) {
			int newPort = newPortName == null ? -1 : newTable.getNames().indexOf(newPortName);
			int existing = existingPortName == null ? -1 : names.indexOf(existingPortName);

			if (model.size() == 0) {
				this.model = newTable.getModel();
				this.names = newTable.getNames();
				return;
			}
			
			if (model.size() > 0 && verbose )
				System.out.println("Connecting " +  newPortName + " to " + existingPortName);
			
			List<ArrayList<Character>> newModel = new ArrayList<ArrayList<Character>>();
			for (int i = 0; i < model.size(); i++) {
				for (int j = 0; j < newTable.getModel().size(); j++) {
					if (isCompatibel(model.get(i).get(existing), newTable.getModel().get(j).get(newPort))) {
						newModel.add(concat(model.get(i), newTable.getModel().get(j)));
					}
				}
			}
			
			this.model = newModel;
			this.names.addAll(newTable.getNames());
		}

		ColoringConnector connect(String name1, String name2) {
			int port1 = name1 == null ? -1 : names.indexOf(name1);
			int port2 = name2 == null ? -1 : names.indexOf(name2);
			List<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>();

			for (int i = 0; i < model.size(); i++) {
				boolean copy = true;
				if ((model.get(i).get(port1) == 'x' && model.get(i).get(port2) != 'x') ||
					(model.get(i).get(port1) != 'x' && model.get(i).get(port2) == 'x'))
					copy = false;

				if (copy)
					result.add(model.get(i));
			}

			return new ColoringConnector(names, result);
		}
	}

