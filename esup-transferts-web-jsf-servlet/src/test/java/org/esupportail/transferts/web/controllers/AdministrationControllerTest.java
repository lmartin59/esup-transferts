package org.esupportail.transferts.web.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.esupportail.transferts.dao.DaoService;
import org.esupportail.transferts.dao.JPADaoServiceImpl;
import org.esupportail.transferts.domain.DomainService;
import org.esupportail.transferts.domain.DomainServiceApogeeImpl;
import org.esupportail.transferts.domain.DomainServiceImpl;
import org.esupportail.transferts.domain.DomainServiceScolarite;
import org.esupportail.transferts.domain.beans.AccueilAnnee;
import org.esupportail.transferts.domain.beans.AccueilResultat;
import org.esupportail.transferts.domain.beans.DatasExterne;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.Fichier;
import org.esupportail.transferts.domain.beans.InfosAccueil;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.PersonnelComposante;
import org.esupportail.transferts.domain.beans.SituationUniversitaire;
import org.esupportail.transferts.domain.beans.TrBac;
import org.esupportail.transferts.domain.beans.Transferts;
import org.esupportail.transferts.domain.beans.User;
import org.esupportail.transferts.services.auth.Authenticator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:META-INF/testApplicationContext.xml")
public class AdministrationControllerTest  {

	@Autowired
	DomainService domainService; 

	@Autowired
	DomainServiceScolarite domainServiceScolarite;

	private EtudiantRef etudiantDepart;
	private EtudiantRef etudiantAccueil;

	EtudiantRef currentDemandeTransferts;

	List<EtudiantRef> listeEtudiants;

	@Before
	public void setUp() throws Exception {
		this.etudiantDepart=new EtudiantRef();

		this.etudiantAccueil=new EtudiantRef();
		etudiantAccueil.setNumeroEtudiant("2005030459J");
		etudiantAccueil.setAnnee(2014);
		etudiantAccueil.setSource("A");
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void  getDemandeTransfertByAnneeAndNumeroEtudiantAndSource()
	{
		System.out.println("===>public void  getDemandeTransfertByAnneeAndNumeroEtudiantAndSource()<===");
		EtudiantRef etu = getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource("20054890", 2015, "D");
		String test = etu.getTransferts().getFichier().getMd5();	
		System.out.println("etu===>"+etu+"<==="); 
	}
	
	//@Test
	public void addDemandeTransfertsDepart()
	{
		System.out.println("===>public void addDemandeTransferts()<===");

		EtudiantRef etu = getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource("20054890", 2015, "D");
		if(etu!=null)
		{
			System.out.println("===>getDomainService().deleteDemandeTransfert(etu, 2015);<===");
			getDomainService().deleteDemandeTransfert(etu, 2015);
		}
		Date date = null;
		String date1 = "31/08/1990";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {
			date = simpleDateFormat.parse(date1);
			System.out.println("date===>"+date+"<===");
		} catch (ParseException e) {
			e.printStackTrace();
		}		

		System.out.println("===>#######################################################################################################################################<===");		

		this.currentDemandeTransferts = getDomainServiceScolarite().getCurrentEtudiantIne("0DDG5F00RQ0", date);
		
		this.currentDemandeTransferts.setAnnee(2015);
		this.currentDemandeTransferts.setSource("D");
		this.currentDemandeTransferts.getAdresse().setCodeCommune("59128");
		this.currentDemandeTransferts.getAdresse().setNomCommune("CAPINGHEM");
		
		this.currentDemandeTransferts.getAdresse().setAnnee(2015);
		this.currentDemandeTransferts.getAdresse().setNumeroEtudiant("20054890");
		
		Transferts t = new Transferts();
		t.setNumeroEtudiant("20054890");
		t.setAnnee(2015);
		t.setRne("0593561A");
		t.setLibRne("Université Lille 3 Charles de Gaulle");
		t.setDept("059");
		t.setLibDept("NORD");
		t.setLibelleTypeDiplome(null);
		t.setDateDemandeTransfert(new Date());
		t.setTypeTransfert("T");
		t.setLibTypeTransfert("Total");
		t.setTemoinTransfertValide(0);
		t.setTemoinOPIWs(null);
		t.setFichier(null);
		
		OffreDeFormationsDTO o = new OffreDeFormationsDTO();
		o.setRne("0593561A");
		o.setAnnee(2015);
		o.setCodeDiplome("CL2LEAB");
		o.setCodeVersionDiplome(150);
		o.setCodeEtape("1ILEAB");
		o.setCodeVersionEtape("150");
		o.setCodeCentreGestion("GAR");
		o.setLibDiplome("LEA anglais / arabe, libVersionEtape=L1 LEA Anglais Arabe");
		o.setCodeComposante("CLA");
		o.setLibComposante("Langues");
		o.setLibCentreGestion("Pôle d Arras");
		o.setCodeNiveau(1);
		o.setLibNiveau("1er année");
		o.setActif(1);
		o.setCodTypDip("81");
		o.setLibTypDip("Licence");
		o.setDateMaj(new Date());
		o.setDepart("oui");
		o.setArrivee("oui");
		
		InfosAccueil ia = new InfosAccueil();
		ia.setAnnee(2015);
		ia.setNumeroEtudiant("20054890");
		
		this.currentDemandeTransferts.setTransferts(t);
		this.currentDemandeTransferts.getTransferts().setOdf(o);
//		this.currentDemandeTransferts.setAccueil(ia);
		this.currentDemandeTransferts.setAccueil(null);
			
		System.out.println("this.currentDemandeTransferts===>"+this.currentDemandeTransferts+"<===");
		
//		getDomainService().addDemandeTransferts(new EtudiantRef());
		getDomainService().addDemandeTransferts(this.currentDemandeTransferts);
		
		EtudiantRef etu2 = getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource("20054890", 2015, "D");
		if(etu2!=null)
		{
			System.out.println("===>getDomainService().deleteDemandeTransfert(etu, 2015);<===");
			getDomainService().deleteDemandeTransfert(etu2, 2015);
		}
	}
	
	@Test
	public void exportDemandeTransfertsAccueil()
	{
		List<EtudiantRef> lEtu2 = getDomainService().getAllDemandesTransfertsByAnnee(2014, "A");
		System.out.println("lEtu2.size()===>"+lEtu2.size()+"<===");
	}	

//	public void execute(JobExecutionContext context) throws JobExecutionException {
//	    // Say Hello to the World and display the date/time
//	   System.out.println("Hello World! - " + new Date());
//	}
	
	//@Test
	public void addDemandeTransfertsAccueil() throws Exception
	{
		System.out.println("===>#######################################################################################################################################<===");

		EtudiantRef etu = this.currentDemandeTransferts = getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource(this.getEtudiantAccueil().getNumeroEtudiant(), this.getEtudiantAccueil().getAnnee(), this.getEtudiantAccueil().getSource());
		
		Date date = null;
		String date1 = "28/03/1980";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {
			date = simpleDateFormat.parse(date1);
			System.out.println("date===>"+date+"<===");
		} catch (ParseException e) {
			e.printStackTrace();
		}		

		System.out.println("===>#######################################################################################################################################<===");		

		this.currentDemandeTransferts = getDomainServiceScolarite().getCurrentEtudiantIne("0997200153K", date);

		if(this.currentDemandeTransferts!=null)
			System.out.println("===>"+this.currentDemandeTransferts.getNumeroIne()+"<===");
		else
			System.out.println("===>this.currentDemandeTransferts.getNumeroIne()==null<===");



		this.currentDemandeTransferts.setSource("A");

		if(this.currentDemandeTransferts.getAdresse().getCodPay().equals("100"))
			this.currentDemandeTransferts.getAdresse().setCodeVilleEtranger(null);
		else
		{
			this.currentDemandeTransferts.getAdresse().setLibPay(getDomainServiceScolarite().getPaysByCodePays(this.currentDemandeTransferts.getAdresse().getCodPay()).getLibPay());
			this.currentDemandeTransferts.getAdresse().setCodePostal(null);
			this.currentDemandeTransferts.getAdresse().setCodeCommune(null);
			this.currentDemandeTransferts.getAdresse().setNomCommune(null);
		}
		this.currentDemandeTransferts.setAnnee(2014);
		this.currentDemandeTransferts.getAdresse().setAnnee(2014);
		this.currentDemandeTransferts.getTransferts().setTemoinTransfertValide(0);
		this.currentDemandeTransferts.getTransferts().setDateDemandeTransfert(new Date());
		this.currentDemandeTransferts.getTransferts().setAnnee(2014);
		this.currentDemandeTransferts.getTransferts().setFichier(null);
		this.currentDemandeTransferts.getAccueil().setAnnee(2014);
		this.currentDemandeTransferts.getAccueil().setNumeroEtudiant(this.currentDemandeTransferts.getNumeroEtudiant());
		this.currentDemandeTransferts.getAccueil().setFrom_source("L");
		
//		System.out.println("===>#######################################################################################################################################<===");		
//		System.out.println("etu.getTransferts().getOdf()===>"+etu.getTransferts().getOdf()+"<===");
//		System.out.println("===>#######################################################################################################################################<===");		
		
		this.currentDemandeTransferts.getTransferts().setOdf(etu.getTransferts().getOdf());
		
//		getDomainService().addDemandeTransferts(this.currentDemandeTransferts);
//		getDomainService().deleteDemandeTransfert(this.currentDemandeTransferts, 2014);

	}

	//	@Test
	public void getAllDemandesTransfertsByAnnee() 
	{
		System.out.println("===>#######################################################################################################################################<===");
		System.out.println("===>public void getAllDemandesTransfertsByAnnee()<===");	

		System.out.println("===>getDomainService().getAllDemandesTransfertsByAnnee(this.getEtudiantAccueil().getAnnee(), this.getEtudiantAccueil().getSource());"
				+this.getEtudiantAccueil().getNumeroEtudiant()+"-----"+this.getEtudiantAccueil().getAnnee()+"-----"+this.getEtudiantAccueil().getSource()+"<===");

		listeEtudiants = getDomainService().getAllDemandesTransfertsByAnnee(this.getEtudiantAccueil().getAnnee(), "A");

		for(EtudiantRef etu : listeEtudiants)
		{
			//			System.out.println("etu.getNomPatronymique()===>"+etu.getNomPatronymique()+"<===");
			//			System.out.println("etu.getAdresse()===>"+etu.getAdresse().toString()+"<===");
			//			System.out.println("etu.getAdresse().getEmail()===>"+etu.getAdresse().getEmail()+"<===");
			//			System.out.println("etu.getAccueil().getSituationUniversitaire()===>"+etu.getAccueil().getSituationUniversitaire()+"<===");
			//			if(etu.getTransferts().getFichier()!=null)
			//				System.out.println("etu.getTransferts().getFichier().getMd5()===>"+etu.getTransferts().getFichier().getMd5()+"<===");
			//			else
			//				System.out.println("etu.getTransferts().getFichier().getMd5()===>null<===");
			//			System.out.println("etu===>"+etu.toString()+"<===");			
		}
	}

	//@Test
	public void goToCurrentDemandeTransfertsAccueil() 
	{
		System.out.println("===>#######################################################################################################################################<===");
		System.out.println("===>public String goToCurrentDemandeTransfertsAccueil()<===");

		System.out.println("===>getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource(this.getEtudiantAccueil().getNumeroEtudiant(), this.getEtudiantAccueil().getAnnee(), this.getEtudiantAccueil().getSource());"
				+this.getEtudiantAccueil().getNumeroEtudiant()+"-----"+this.getEtudiantAccueil().getAnnee()+"-----"+this.getEtudiantAccueil().getSource()+"<===");		
		this.currentDemandeTransferts = getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource(this.getEtudiantAccueil().getNumeroEtudiant(), this.getEtudiantAccueil().getAnnee(), this.getEtudiantAccueil().getSource());

		if (this.currentDemandeTransferts != null) 
		{
			System.out.println("aaaaa===>if (this.currentDemandeTransferts != null)<===");

			List<DatasExterne> listeDatasEterneNiveau2 = getDomainService().getAllDatasExterneByIdentifiantAndNiveau(this.currentDemandeTransferts.getNumeroIne(), 2);

			List<DatasExterne> listeDatasEterneNiveau3 = getDomainService().getAllDatasExterneByIdentifiantAndNiveau(this.currentDemandeTransferts.getNumeroIne(), 3);

			PersonnelComposante droitPC = getDomainService().getDroitPersonnelComposanteByUidAndSourceAndAnneeAndCodeComposante("farid.aitkarra",
					"A", 
					2014, 
					this.currentDemandeTransferts.getTransferts().getOdf().getCodeComposante());					

			Fichier file=null;
			//
			if(this.currentDemandeTransferts.getTransferts().getFichier()!=null)
				file = getDomainService().getFichierByIdAndAnneeAndFrom(this.currentDemandeTransferts.getTransferts().getFichier().getMd5(),this.getEtudiantAccueil().getAnnee(), this.currentDemandeTransferts.getSource());

			if(file!=null && file.getNom().equals("ETABLISSEMENT_PARTENAIRE"))
			{
				file = getDomainService().getFichierDefautByAnneeAndFrom(2014, "A");
				this.currentDemandeTransferts.getTransferts().setFichier(file);
			}




			List<TrBac> listeBacDTO = getDomainServiceScolarite().recupererBacOuEquWS(this.currentDemandeTransferts.getAccueil().getCodeBac());

			System.out.println("file===>"+file+"<===");

			System.out.println("this.currentDemandeTransferts===>"+this.currentDemandeTransferts.toString()+"<===");

			System.out.println("this.currentDemandeTransferts===>"+this.currentDemandeTransferts.toString()+"<===");	
		}
	}		

	//@Test
	//	public void testGetInfosEtudiant()
	//	{
	//		System.out.println("getDomainServiceScolarite().getEtablissementByDepartement(059)===>"+getDomainServiceScolarite().getEtablissementByDepartement("059")+"<===");
	//	}

	//	@Test
	//	public void testGetUser()
	//	{
	//		User user = null;
	//		try {
	//			user = authenticator.getUser();
	//		} catch (Exception e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		System.out.println("user===>"+user+"<===");
	//	}

	public DomainService getDomainService() {
		return domainService;
	}

	public void setDomainService(DomainService domainService) {
		this.domainService = domainService;
	}

	public DomainServiceScolarite getDomainServiceScolarite() {
		return domainServiceScolarite;
	}

	public void setDomainServiceScolarite(
			DomainServiceScolarite domainServiceScolarite) {
		this.domainServiceScolarite = domainServiceScolarite;
	}

	public EtudiantRef getCurrentDemandeTransferts() {
		return currentDemandeTransferts;
	}

	public void setCurrentDemandeTransferts(EtudiantRef currentDemandeTransferts) {
		this.currentDemandeTransferts = currentDemandeTransferts;
	}

	public EtudiantRef getEtudiantDepart() {
		return etudiantDepart;
	}

	public void setEtudiantDepart(EtudiantRef etudiantDepart) {
		this.etudiantDepart = etudiantDepart;
	}

	public EtudiantRef getEtudiantAccueil() {
		return etudiantAccueil;
	}

	public void setEtudiantAccueil(EtudiantRef etudiantAccueil) {
		this.etudiantAccueil = etudiantAccueil;
	}

	public List<EtudiantRef> getListeEtudiants() {
		return listeEtudiants;
	}

	public void setListeEtudiants(List<EtudiantRef> listeEtudiants) {
		this.listeEtudiants = listeEtudiants;
	}
}
