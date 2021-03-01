package br.com.agent.plan.designpattern;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import br.com.agent.belief.ProprietyClass;
import jadex.bdiv3.runtime.BDIFailureException;

public class StrategyPlan extends DesignPattern implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public StrategyPlan() {

	}

	public boolean isApplicable(ProprietyClass object) {

		Map<MethodDeclaration, List<Statement>> mapaMetodosAnalisados = new HashMap<>();

		try {
			List<?> members = object.getType().getMembers().stream().filter(row -> row instanceof MethodDeclaration)
					.collect(Collectors.toList());
			for (Object methodRead : members) {

				MethodDeclaration method = (MethodDeclaration) methodRead;
				if (method.isAbstract() || (method.isConstructorDeclaration() && method.isPrivate())) {
					return false;
				}
				List<Parameter> parametersMethod = method.getParameters();
				List<Statement> statementsIf = new ArrayList<>();

				if (parametersMethod == null || parametersMethod.isEmpty()) {
					System.out
							.println(" -- Método:" + method.getName() + " -- não tem parametros não é usado strategy");
					continue;
				}
				if (method.getBody() != null && method.getBody().isPresent() && !method.getBody().get().isEmpty()) {
					List<Statement> rowsMethod = method.getBody().get().getStatements();
					if (rowsMethod != null && !rowsMethod.isEmpty()) {
						for (Statement row : rowsMethod) {
							if (row instanceof IfStmt) {
								IfStmt conditional = ((IfStmt) row);
								List<Node> fieldsConditional = conditional.getCondition().getChildNodes();
								for (Node campo : fieldsConditional) {
									for (Parameter parameter : parametersMethod) {
										/** adaptado para não pegar condições com OR ou AND */
										String txtCondicao = conditional.getCondition().toString();
										if (parameter.getNameAsString().toString().equals(campo.toString())
												&& !txtCondicao.contains("||") && !txtCondicao.contains("&&")
												&& !txtCondicao.contains("exist") && !txtCondicao.contains("this")
												&& !txtCondicao.contains("null") && !txtCondicao.contains("equals")
												&& !txtCondicao.contains("ANY")
												&& !txtCondicao.contains(">")
												&& !txtCondicao.contains("SuppressWarnings")
												&& !txtCondicao.contains("try")
												&& txtCondicao.contains("==")
												&& conditional.getThenStmt().toBlockStmt().isPresent()
												&& conditional.getThenStmt().toBlockStmt().get().toString().contains("return")) {
											if (conditional == null || conditional.getThenStmt() == null
													|| conditional.getThenStmt().toBlockStmt() == null
													|| !conditional.getThenStmt().toBlockStmt().isPresent()
													|| conditional.getThenStmt().toBlockStmt().get() == null
													|| conditional.getThenStmt().toBlockStmt().get()
															.getStatements() == null) {
												continue;
											}
											String bodyIf = conditional.getThenStmt().toBlockStmt().get().toString();
											int rowsIf = conditional.getThenStmt().toBlockStmt().get().getStatements()
													.size();

											if (rowsIf == 1 && bodyIf.contains("new")) {
												continue;
											}
											String[] separaCondicao = conditional.getCondition().toString().split("=");
											String linhaSemReplace = bodyIf.replace("return", "");
											boolean soNumeroEmCondicao = false;
											if(separaCondicao.length == 2) {
												soNumeroEmCondicao = this.isNumeric(separaCondicao[1].toString().trim());
											}else if(separaCondicao.length == 3) {
												soNumeroEmCondicao = this.isNumeric(separaCondicao[2].toString().trim());
											}
											System.out.println("Linha IF analisada pelo strategy: " + conditional.getCondition().toString());
											if (this.isNumeric(linhaSemReplace)) {
												System.out.println("Retorno do IF só tem números !" + conditional.getCondition().toString());
												continue;
											}else if(soNumeroEmCondicao){
												System.out.println("Retorrno do IF só tem números !" + conditional.getCondition().toString());
												continue;
											}if (bodyIf.contains("if")) {
												continue;
											}
											statementsIf.add(conditional);
										}
									}
								}
							}
						}
						if (statementsIf != null && !statementsIf.isEmpty()) {
							mapaMetodosAnalisados.put(method, statementsIf);
						}
					}
				}
			}
			object.setMapMethodsAnalyzed(mapaMetodosAnalisados);
			return object.getMapMethodsAnalyzed().size() > 0;
		} catch (BDIFailureException ex) {
			throw new IllegalStateException("Erro - causado por: " + ex.getMessage());
		}
	}

	public static boolean isNumeric(String str) {
		return str != null && str.matches("[-+]?\\d*\\.?\\d+");
	}

	public void applyMethod(ProprietyClass object) {
		String nomePacote = object.getCu().getPackageDeclaration().get().getNameAsString().toString();
		String separaCondicao[] = null;
		object.getType().setBlockComment("Class modified to Strategy pattern...");

		Map<MethodDeclaration, List<Statement>> ondeModificar = object.getMapMethodsAnalyzed();
		for (Map.Entry<MethodDeclaration, List<Statement>> entrada : ondeModificar.entrySet()) {
			MethodDeclaration metodoModificado = entrada.getKey();
			List<Statement> condicionaisEncontrados = entrada.getValue();

			IfStmt ifStmt1 = (IfStmt) condicionaisEncontrados.get(0);
			String retorno1 = ifStmt1.getThenStmt().toString().replaceAll("[0-9]", "").replaceAll("[-+=*;%$#@!{}.]", "")
					.replace("return", "");
			String nomeParametro = retorno1.trim();
			if (!nomeParametro.isEmpty()) {
				if (nomeParametro.contains("new") || nomeParametro.contains("false")) {
					continue;
				}

				/** precisa criar antes a classe abstract */
				CompilationUnit cu1 = new CompilationUnit();
				ClassOrInterfaceDeclaration type1 = cu1.addClass("Strategy");
				MethodDeclaration method1 = new MethodDeclaration();
				method1.setName(metodoModificado.getName());
				method1.setType(metodoModificado.getType());
				method1.setModifiers(EnumSet.of(Modifier.PUBLIC));
				method1.addParameter(metodoModificado.getType(), nomeParametro);
				type1.addMember(method1);
				type1.addModifier(Modifier.ABSTRACT);
				saveContent(object.getParentFile().getPath() + "/Strategy.java", cu1.toString());

				for (Statement statement : condicionaisEncontrados) {
					/** criar um arquivo ConcreteStrategy + code com cada if */
					IfStmt ifStmt = (IfStmt) statement;
					IfStmt elseThen = null;

					while (true) {
						System.out.println("Entrando no laço while do strategy classe: " + object.getAbsolutePath());
						String condicao = ifStmt.getCondition().toString();
						System.out.println("Condicional: " + condicao);
						if (condicao.contains(" == ")) {
							separaCondicao = condicao.split(" == ");
						} else if (condicao.contains(" >= ")) {
							separaCondicao = condicao.split(" >= ");
						} else if (condicao.contains(" <= ")) {
							separaCondicao = condicao.split(" <= ");
						} else if (condicao.contains(" != ")) {
							separaCondicao = condicao.split(" != ");
						} else if (condicao.contains("equals")) {
							separaCondicao = new String[2];
							separaCondicao[0] = ifStmt.getCondition().getChildNodes().get(0).toString();
							separaCondicao[1] = ifStmt.getCondition().getChildNodes().get(2).toString();
						}
						boolean condicaoCompletaIf = separaCondicao != null && separaCondicao.length == 2
								&& separaCondicao[1] != null && !isNumeric(separaCondicao[1].toString());
						if (condicaoCompletaIf) {

							String nomeClasse = separaCondicao[1].replaceAll("'", "").toString().replace('"', ' ')
									.trim();
							String nomeClasseArquivo = "ConcreteStrategy" + nomeClasse.toUpperCase();

							CompilationUnit cu = new CompilationUnit();
							System.out.println("Nome do pacote usado para strategy:" + nomePacote);
							cu1.addImport(nomePacote + ".Strategy");
							cu.setPackageDeclaration(object.getCu().getPackageDeclaration().get());
							cu.setBlockComment("Concrete Strategy Class generated based on the Strategy standard");
							ClassOrInterfaceDeclaration type = cu.addClass(nomeClasseArquivo);

							/** criar um método */
							String textoIF = ifStmt.getThenStmt().toString().replace("{", "").replace("}", "");
							System.out.println("Texto do IF: " + textoIF);
							BlockStmt block = new BlockStmt();
							block.addStatement(textoIF);
							MethodDeclaration method = new MethodDeclaration();
							method.setName(metodoModificado.getName());
							method.setType(metodoModificado.getType());
							method.setModifiers(EnumSet.of(Modifier.PUBLIC));
							method.addParameter(metodoModificado.getType(), nomeParametro);
							method.setBody(block);
							type.addMember(method);
							type.addExtends("Strategy");

							saveContent(object.getParentFile().getPath() + "/" + nomeClasseArquivo + ".java",
									cu.toString());

						}

						/**
						 * na última linha o else vai para o lugar do if fazendo assim rodar todos os
						 * possíveis else if
						 */
						if (ifStmt.getElseStmt().isPresent() && ifStmt.getElseStmt().get() instanceof IfStmt) {
							System.out.println("Pulando para o else.");
							elseThen = (IfStmt) ifStmt.getElseStmt().get();
							ifStmt = elseThen;
						} else {
							break;
						}
					}
				}
			}
	
			/** reescreve Método da classe refatorada */
			BlockStmt block = new BlockStmt();
			block.addStatement("return strategy." + metodoModificado.getName() + "("
					+ nomeParametro.toString().replace("(", "").replace(")", "").replace(">", "") + ");");
			metodoModificado.setBody(block);

			Parameter parametro = new Parameter();
			parametro.setName("strategy");
			parametro.setType("Strategy");
			metodoModificado.setParameter(0, parametro);

			object.getCu().addImport(nomePacote + ".Strategy");
			saveContent(object.getAbsolutePath(), object.getCu().toString());
		}

		super.setApplied(true);

	}

}
