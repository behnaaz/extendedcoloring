package org.behnaz.rcsp;

import java.util.ArrayList;
import java.util.List;


public class ColoringConnector extends AbstractConnector {
		List<ArrayList<Character>> model;

		public ColoringConnector(String[] names, List<ArrayList<Character>> model) {
			super(names);
			this.model = model;
		}

		public ColoringConnector(List<String> names, List<ArrayList<Character>> model) {
			super(names);
			this.model =  model;
		}

		
		public List<ArrayList<Character>> getModel() {
			return model;
		}

		public ColoringConnector ground(String... boundaries) {
			List<ArrayList<Character>> result = new ArrayList<>();
			for (int i = 0; i < model.size(); i++) {
				boolean copy = true;
				for (int j = 0; j < boundaries.length; j++) {
					if (model.get(i).get(variableNames.indexOf(boundaries[j])) == 'o') {
						copy = false;
						break;
					}
				}
				if (copy)
					result.add(model.get(i));
			}
			return new ColoringConnector(variableNames, result);
		}
		
		private String texify(Character ch) {
			if (!isTex)
				return ch.toString();

			switch (ch) {
				case 'x':
					return " $\\times$ ";
				case 'o':
					return " $\\circ$ ";
				case '.':
					return " $\\bullet$ ";
				default:
					return "UNDEF";
			}
		}

		public void output() {
			for (int i = 0; i < model.size(); i++) {
				if (i == 0) {
					Starter.log("Nodes : "+ model.get(i).size() + " Lines : " + model.size());
					for (int j = 0; j < model.get(i).size(); j++) {
						String out = texify(variableNames.get(j));
						if (j != 0)
							out = spaced(variableNames.get(j).length(), out);
						Starter.log(out);

					}
					Starter.log(newLine);
				}
				for (int j = 0; j < model.get(i).size(); j++) {
					String out = texify(model.get(i).get(j));
					if (j != 0)
						out = spaced(variableNames.get(j).length()+1, out);

					Starter.log(out);

				}
				Starter.log(newLine);
			}
		}

		private String texify(String name) {
			if (!isTex)
				return name;

			return name.replaceAll("(.*)(\\d+)", " \\$$1\\_$2\\$ ");
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

		public void add(ColoringConnector newTable, String newPortName, String existingPortName) {
			int newPort = newPortName == null ? -1 : newTable.getVariableNames().indexOf(newPortName);
			int existing = existingPortName == null ? -1 : variableNames.indexOf(existingPortName);

			if (!model.isEmpty()) {
				this.model = newTable.getModel();
				this.variableNames = newTable.getVariableNames();
				return;
			}
			
			if (!model.isEmpty())
				Starter.log("Connecting " +  newPortName + " to " + existingPortName);
			
			List<ArrayList<Character>> newModel = new ArrayList<>();
			for (int i = 0; i < model.size(); i++) {
				for (int j = 0; j < newTable.getModel().size(); j++) {
					if (isCompatibel(model.get(i).get(existing), newTable.getModel().get(j).get(newPort))) {
						newModel.add(concat(model.get(i), newTable.getModel().get(j)));
					}
				}
			}
			
			this.model = newModel;
			this.variableNames.addAll(newTable.getVariableNames());
		}

		public ColoringConnector connect(String name1, String name2) {
			int port1 = name1 == null ? -1 : variableNames.indexOf(name1);
			int port2 = name2 == null ? -1 : variableNames.indexOf(name2);
			List<ArrayList<Character>> result = new ArrayList<>();

			for (int i = 0; i < model.size(); i++) {
				boolean copy = true;
				if ((model.get(i).get(port1) == 'x' && model.get(i).get(port2) != 'x') ||
					(model.get(i).get(port1) != 'x' && model.get(i).get(port2) == 'x'))
					copy = false;

				if (copy)
					result.add(model.get(i));
			}

			return new ColoringConnector(variableNames, result);
		}
	}