package edu.harvard.iq.dataverse.pidproviders.handle.fake;

import edu.harvard.iq.dataverse.DvObject;
import edu.harvard.iq.dataverse.GlobalId;
import edu.harvard.iq.dataverse.pidproviders.AbstractPidProvider;
import edu.harvard.iq.dataverse.pidproviders.handle.HandlePidProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A "fake" implementation of a Handle provider, for testing.
 *  Note that there is no AbstractHandleProvider, we could make one if we incorporated 
 *  this FakeHandleProvider into the Dataverse application.
 */
public class FakeHandleProvider extends AbstractPidProvider {

    private static final Logger logger = Logger.getLogger(HandlePidProvider.class.getCanonicalName());

    public static final String HDL_PROTOCOL = "hdl";
    public static final String TYPE = "FAKE"; //"hdl";
    public static final String HTTP_HDL_RESOLVER_URL = "http://hdl.handle.net/";
    public static final String HDL_RESOLVER_URL = "https://hdl.handle.net/";



    int handlenetIndex;
    private boolean isIndependentHandleService;
    private String authHandle;
    private String keyPath;
    private String keyPassphrase;

    public FakeHandleProvider(String id, String label, String authority, String shoulder, String identifierGenerationStyle,
                             String datafilePidFormat, String managedList, String excludedList, int index, boolean isIndependentService, String authHandle, String path, String passphrase) {
        super(id, label, HDL_PROTOCOL, authority, shoulder, identifierGenerationStyle, datafilePidFormat, managedList, excludedList);
        this.handlenetIndex = index;
        this.isIndependentHandleService = isIndependentService;
        this.authHandle = authHandle;
        this.keyPath = path;
        this.keyPassphrase = passphrase;
    }


    @Override
    public boolean alreadyRegistered(GlobalId globalId, boolean noProviderDefault) throws Exception {
        boolean existsLocally = !pidProviderService.isGlobalIdLocallyUnique(globalId);
        return existsLocally ? existsLocally : noProviderDefault;
    }

    @Override
    public boolean registerWhenPublished() {
        return false;
    }

    @Override
    public List<String> getProviderInformation() {
        return List.of(getId(), "https://dataverse.org");
    }

    @Override
    public String createIdentifier(DvObject dvObject) throws Throwable {
        if(dvObject.getIdentifier() == null || dvObject.getIdentifier().isEmpty() ){
            dvObject = generatePid(dvObject);
        }
        return dvObject.getIdentifier();
    }

    @Override
    public Map<String, String> getIdentifierMetadata(DvObject dvObject) {
        Map<String, String> map = new HashMap<>();
        return map;
    }

    @Override
    public String modifyIdentifierTargetURL(DvObject dvo) throws Exception {
        return  "fakeModifyIdentifierTargetURL";
    }

    @Override
    public void deleteIdentifier(DvObject dvo) throws Exception {
        // no-op
    }

    @Override
    public boolean publicizeIdentifier(DvObject dvObject) {
        if(dvObject.isInstanceofDataFile() && dvObject.getGlobalId()==null) {
            generatePid(dvObject);
        }
        return true;
    }

    @Override
    public String getUrlPrefix() {
        return HDL_RESOLVER_URL;
    }

    @Override
    public String getProviderType() {
        return TYPE;
    }
}
