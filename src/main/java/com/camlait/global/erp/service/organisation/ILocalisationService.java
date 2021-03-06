package com.camlait.global.erp.service.organisation;

import java.util.Collection;

import com.camlait.global.erp.domain.organisation.Centre;
import com.camlait.global.erp.domain.organisation.Localisation;
import com.camlait.global.erp.domain.organisation.Region;
import com.camlait.global.erp.domain.organisation.Secteur;
import com.camlait.global.erp.domain.organisation.Zone;
import com.camlait.global.erp.domain.exception.GlobalErpServiceException;

public interface ILocalisationService {
    
	Localisation ajouterLocalisation(Localisation local) throws GlobalErpServiceException, IllegalArgumentException;
    
	Localisation modifierLocalisation(Localisation local) throws GlobalErpServiceException, IllegalArgumentException;
    
	<T> T obtenirLocalisation(Class<T> entityClass,Long localId) throws GlobalErpServiceException, IllegalArgumentException, ClassCastException;
    
	<T> T obtenirLocalisation(Class<T> entityClass, String codeLocalisation)
            throws GlobalErpServiceException, IllegalArgumentException, ClassCastException;
            
    Collection<Centre> listerCentre() throws GlobalErpServiceException, IllegalArgumentException;
    
    Collection<Region> listerRegion(Centre centre) throws GlobalErpServiceException, IllegalArgumentException;
    
    Collection<Secteur> listerSecteur(Region region) throws GlobalErpServiceException, IllegalArgumentException;
    
    Collection<Zone> listerZone(Secteur secteur) throws GlobalErpServiceException, IllegalArgumentException;
    
    void supprimerLocalisation(Long localId) throws GlobalErpServiceException, IllegalArgumentException;
}
