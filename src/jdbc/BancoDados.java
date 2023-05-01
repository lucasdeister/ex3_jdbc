package jdbc;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;

public class BancoDados implements InterfaceBancoDados {

	public static Connection c;
	public static Scanner reader = new Scanner(System.in);
	public static Log lg;	

	public static void main(String[] args) throws IOException {
		
		lg = new Log("Log.txt");
		
		try {
			lg.logger.setLevel(Level.INFO);
		}
		catch(Exception e){
			lg.logger.info("Exception:" + e.getMessage());
			e.printStackTrace();
		}
		
		lg.logger.info("\n O programa foi iniciado, escolha uma opção para conectar com o BD:");
		exibirMenu();
	}

	public static void exibirMenu() {
		int numberSelected = 0;

		while (numberSelected != 5) {
			System.out.println("1 - Inserir");
			System.out.println("2 - Alterar");
			System.out.println("3 - Excluir");
			System.out.println("4 - Consultar");
			System.out.println("5 - Desconectar");

			numberSelected = reader.nextInt();

			processarDados(numberSelected);
		}
	}

	public static void processarDados(int codigo) {
		final String db_url = "jdbc:mysql://localhost:3306/bancojdbc";
		final String query_consulta = "SELECT * FROM pessoa";
		final String db_user = "root";
		final String db_password = "";
		BancoDados bd = new BancoDados();

		if (codigo == 5) {
			bd.desconectar();
		} else {
			bd.conectar(db_url, db_user, db_password);
			do {
				switch (codigo) {
				case 1:
					String name;
					String email;
					System.out.println("Informe um nome : ");
					reader.nextLine();
					name = reader.nextLine();
					System.out.println("Informe um email : ");
					email = reader.nextLine();

					bd.inserirAlterarExcluir("INSERT INTO pessoa(nome, email) VALUES (" + "'" + name + "'" + "," + "'"
							+ email + "'" + ")");
					break;

				case 2:
					bd.consultar(query_consulta);
					System.out.println("Selecione o id do usuário que deseja alterar :");
					int idUser = reader.nextInt();
					String nameAlt;
					String emailAlt;
					System.out.println("Informe o novo nome");
					reader.nextLine();
					nameAlt = reader.nextLine();
					System.out.println("Informe o novo email");
					emailAlt = reader.nextLine();

					bd.inserirAlterarExcluir("UPDATE pessoa SET nome = " + "'" + nameAlt + "'" + "," + "email = " + "'"
							+ emailAlt + "'" + " WHERE id = " + idUser);
					break;

				case 3:
					bd.consultar(query_consulta);
					System.out.println("Selecione o id do usuário que você deseja deletar");
					int idUserDel = reader.nextInt();

					bd.inserirAlterarExcluir("DELETE FROM pessoa WHERE id = " + idUserDel);
					break;

				case 4:
					bd.consultar(query_consulta);
					break;
				}
				exibirMenu();
				codigo = reader.nextInt();
			} while (codigo != 5);
		}
	}

	@Override
	public void conectar(String db_url, String db_user, String db_password) {

		try {
			c = DriverManager.getConnection(db_url, db_user, db_password);
			lg.logger.info("Conectado ao DB");

		} catch (SQLException e) {
			e.printStackTrace();
			lg.logger.info("Não Foi possivel conectar ao DB");
		}
	}

	@Override
	public void desconectar() {
		try {
			if (c != null) {
				c.close();
				lg.logger.info("Você fez logout com sucesso!");
			}
		} catch (SQLException e) {
			lg.logger.info("Não Foi possivel conectar ao DB");
			e.printStackTrace();
		}
	}

	@Override
	public void consultar(String db_query) {
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(db_query);
			while (rs.next()) {
				System.out.println(rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3));
			}
			lg.logger.info("Você realizou a consulta com sucesso!");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			lg.logger.info("Não Foi possivel conectar ao DB");
			e.printStackTrace();
		}
	}

	@Override
	public int inserirAlterarExcluir(String db_query) {
		int rowsAffected = 0;
		try {
			Statement stmt = c.createStatement();
			rowsAffected = stmt.executeUpdate(db_query);
		
			if(db_query.contains("INSERT")) {
				lg.logger.info("Você realizou a inserção com sucesso!");
			}else if(db_query.contains("UPDATE")) {
				lg.logger.info("Você realizou a atualização com sucesso!");
			}
			else if(db_query.contains("DELETE")) {
				lg.logger.info("Você realizou a remoção com sucesso!");
			}
			
			stmt.close();
			
		} catch (SQLException e) {
			lg.logger.info("Não foi possível executar a operação de inserção/alteração/exclusão no banco de dados.");
			e.printStackTrace();
		}
		lg.logger.info(rowsAffected + " linhas foram afetadas.");
		lg.logger.info("\n Operações realizadas com sucesso!");
		return rowsAffected;
	}
}
