package com.camlait.global.erp.service.document;

import static com.camlait.global.erp.domain.config.GlobalAppConstants.verifyIllegalArgumentException;
import static com.camlait.global.erp.domain.config.GlobalAppConstants.verifyObjectNoFindException;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.camlait.global.erp.dao.document.DocumentDao;
import com.camlait.global.erp.dao.document.LigneDeDocumentTaxeDao;
import com.camlait.global.erp.dao.document.LigneDocumentDao;
import com.camlait.global.erp.domain.document.Document;
import com.camlait.global.erp.domain.document.LigneDeDocument;
import com.camlait.global.erp.domain.document.LigneDeDocumentTaxe;
import com.camlait.global.erp.domain.document.commerciaux.Taxe;
import com.camlait.global.erp.domain.document.commerciaux.vente.FactureClient;
import com.camlait.global.erp.domain.util.Compute;
import com.camlait.global.erp.service.util.IUtilService;

@Transactional
public class DocumentService implements IDocumentService {
    
    @Autowired
    private DocumentDao documentDao;
    
    @Autowired
    private LigneDocumentDao ligneDeDocumentDao;
    
    @Autowired
    private LigneDeDocumentTaxeDao ligneDeDocumentTaxeDao;
    
    @Autowired
    private IUtilService utilService;
    
    @Override
    public Document ajouterDocument(Document document) {
        verifyIllegalArgumentException(document, "document");
        document.setCodeDocument(utilService.genererCode(document));
        documentDao.save(document);
        ajouterLigneDocument(document.getLigneDocuments());
        return document;
    }
    
    @Override
    public Document modifierDocument(Document document) {
        verifyIllegalArgumentException(document, "document");
        document.setDerniereMiseAJour(new Date());
        documentDao.saveAndFlush(document);
        return document;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T obtenirDocument(Class<T> entityClass, Long documentId) {
        verifyIllegalArgumentException(documentId, "documentId");
        final Document d = documentDao.findOne(documentId);
        verifyObjectNoFindException(d, entityClass, documentId);
        Hibernate.initialize(d.getLigneDocuments());
        if (d instanceof FactureClient) {
            Hibernate.initialize(((FactureClient) d).getFactureReglements());
        }
        return (T) d;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T obtenirDocument(Class<T> entityClass, String codeDocument) {
        verifyIllegalArgumentException(codeDocument, "codeDocument");
        final List<Document> documents = documentDao.findByCodeDocument(codeDocument, new PageRequest(0, 1)).getContent();
        final Document d = (documents.isEmpty()) ? null : documents.get(0);
        verifyObjectNoFindException(d, entityClass, codeDocument);
        Hibernate.initialize(d.getLigneDocuments());
        if (d instanceof FactureClient) {
            Hibernate.initialize(((FactureClient) d).getFactureReglements());
        }
        return (T) d;
    }
    
    @Override
    public void supprimerDocument(Long documentId) {
        documentDao.delete(obtenirDocument(Document.class, documentId));
    }
    
    @Override
    public Page<Document> listerDocument(Date debut, Date fin, Pageable p) {
        return documentDao.listerDocument(debut, fin, p);
    }
    
    @Override
    public LigneDeDocument ajouterLigneDocument(LigneDeDocument ligne) {
        verifyIllegalArgumentException(ligne, "ligne");
        ligneDeDocumentDao.save(ligne);
        ajouterLigneDeDocumentTaxe(ligne);
        return ligne;
    }
    
    @Override
    public Collection<LigneDeDocument> ajouterLigneDocument(Collection<LigneDeDocument> lignes) {
        verifyIllegalArgumentException(lignes, "lignes");
        ligneDeDocumentDao.save(lignes);
        ajouterLigneDeDocumentTaxe(lignes);
        return lignes;
    }
    
    @Override
    public LigneDeDocument modifierLigneDocument(LigneDeDocument ligne) {
        verifyIllegalArgumentException(ligne, "ligne");
        ligne.setDerniereMiseAJour(new Date());
        ligneDeDocumentDao.saveAndFlush(ligne);
        return ligne;
    }
    
    @Override
    public LigneDeDocument obtenirLigneDocument(Long ligneId) {
        verifyIllegalArgumentException(ligneId, "ligneId");
        LigneDeDocument ld = ligneDeDocumentDao.findOne(ligneId);
        verifyObjectNoFindException(ld, LigneDeDocument.class, ligneId);
        Hibernate.initialize(ld.getLigneDeDocumentTaxes());
        return ld;
    }
    
    @Override
    public void supprimerLigneDocument(Long ligneId) {
        ligneDeDocumentDao.delete(obtenirLigneDocument(ligneId));
    }
    
    @Override
    public void supprimerLigneDocument(Document document) {
        ligneDeDocumentDao.delete(document.getLigneDocuments());
    }
    
    @Override
    public LigneDeDocumentTaxe ajouterLigneDeDocumentTaxe(LigneDeDocumentTaxe ligneDeDocumentTaxe) {
        verifyIllegalArgumentException(ligneDeDocumentTaxe, "ligneDeDocumentTaxe");
        ligneDeDocumentTaxeDao.save(ligneDeDocumentTaxe);
        return ligneDeDocumentTaxe;
    }
    
    @Override
    public LigneDeDocumentTaxe modifierLigneDeDocumentTaxe(LigneDeDocumentTaxe ligneDeDocumentTaxe) {
        verifyIllegalArgumentException(ligneDeDocumentTaxe, "ligneDeDocumentTaxe");
        ligneDeDocumentTaxe.setDerniereMiseAJour(new Date());
        ligneDeDocumentTaxeDao.saveAndFlush(ligneDeDocumentTaxe);
        return ligneDeDocumentTaxe;
    }
    
    @Override
    public LigneDeDocumentTaxe obtenirLigneDeDocumentTaxe(Long ligneDeDocumentTaxeId) {
        verifyIllegalArgumentException(ligneDeDocumentTaxeId, "ligneDeDocumentTaxeId");
        LigneDeDocumentTaxe l = ligneDeDocumentTaxeDao.findOne(ligneDeDocumentTaxeId);
        verifyObjectNoFindException(l, LigneDeDocumentTaxe.class, ligneDeDocumentTaxeId);
        return l;
     }
    
    @Override
    public void spprimerLigneDeDocumentTaxe(Long ligneDeDocumentTaxeId) {
        ligneDeDocumentTaxeDao.delete(obtenirLigneDeDocumentTaxe(ligneDeDocumentTaxeId));
    }
    
    /**
     * Ajout des taxe a une ligne de document.
     * 
     * @param ligneDeDocument
     */
    private void ajouterLigneDeDocumentTaxe(LigneDeDocument ligneDeDocument) {
        verifyIllegalArgumentException(ligneDeDocument, "ligneDeDocument");
        ligneDeDocument.getProduit().getProduitTaxes().parallelStream().forEach(pt -> {
            LigneDeDocumentTaxe l = new LigneDeDocumentTaxe();
            l.setLigneDeDocument(ligneDeDocument);
            l.setTaxe(pt.getTaxe());
            l.setTauxDeTaxe(pt.getTaxe().getValeurPourcentage());
            ajouterLigneDeDocumentTaxe(l);
        });
    }
    
    /**
     * Ajout des taxes aux lignes de document de maniere groupee.
     * 
     * @param lignes
     */
    private void ajouterLigneDeDocumentTaxe(Collection<LigneDeDocument> lignes) {
        verifyIllegalArgumentException(lignes, "lignes");
        lignes.parallelStream().forEach(ligne -> ajouterLigneDeDocumentTaxe(ligne));
    }
    
    @Override
    public double chiffreAffaireHorsTaxe(Document document) {
        verifyIllegalArgumentException(document, "document");
        final Compute caht = new Compute();
        document.getLigneDocuments().stream().forEach(l -> {
            caht.cummuler(l.getPrixunitaiteLigne() * l.getQuantiteLigne());
        });
        return caht.getValue();
    }
    
    @Override
    public double valeurTotaleTaxe(Document document) {
        verifyIllegalArgumentException(document, "document");
        final Compute taxe = new Compute();
        document.getLigneDocuments().stream().forEach(l -> {
            l.getLigneDeDocumentTaxes().stream().forEach(ldt -> {
                taxe.cummuler(l.getPrixunitaiteLigne() * l.getQuantiteLigne() * ldt.getTauxDeTaxe());
            });
        });
        return taxe.getValue();
    }
    
    @Override
    public double chiffreAffaireTTC(Document document) {
        return chiffreAffaireHorsTaxe(document) + valeurTotaleTaxe(document);
    }
    
    @Override
    public double valeurTaxe(Taxe taxe, Document document) {
        verifyIllegalArgumentException(document, "document");
        final Compute valeur = new Compute();
        document.getLigneDocuments().stream().forEach(ld -> {
            ld.getLigneDeDocumentTaxes().stream().filter(ldt -> ldt.getTaxe().getTaxeId() == taxe.getTaxeId())
                    .forEach(ldt -> {
                valeur.cummuler(ld.getPrixunitaiteLigne() * ld.getQuantiteLigne() * ldt.getTauxDeTaxe());
            });
        });
        return valeur.getValue();
    }
    
    @Override
    public double valeurMarge(Document document) {
        verifyIllegalArgumentException(document, "document");
        final Compute marge = new Compute();
        document.getLigneDocuments().stream().forEach(l -> {
            marge.cummuler(l.getProduit().getPrixUnitaireMarge() * l.getQuantiteLigne());
        });
        return marge.getValue();
    }
    
}
