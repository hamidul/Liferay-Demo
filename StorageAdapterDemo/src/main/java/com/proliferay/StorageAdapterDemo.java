package com.proliferay;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONSerializer;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.BaseStorageAdapter;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageAdapter;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidator;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.File;
import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author hamidul
 */
@Component(
	immediate = true,
	property = {
		// TODO enter required service properties
	},
	service = StorageAdapter.class
)
public class StorageAdapterDemo extends  BaseStorageAdapter {

	@Override
	public String getStorageType() {
		return "File System";
	}

	@Override
	protected long doCreate(long companyId, long ddmStructureId, DDMFormValues ddmFormValues,
			ServiceContext serviceContext) throws Exception {
		
		
		validate(ddmFormValues, serviceContext);

		long fileId = _counterLocalService.increment();

		DDMStructureVersion ddmStructureVersion = _ddmStructureVersionLocalService
				.getLatestStructureVersion(ddmStructureId);

		long classNameId = PortalUtil.getClassNameId(StorageAdapterDemo.class.getName());

		_ddmStorageLinkLocalService.addStorageLink(classNameId, fileId, ddmStructureVersion.getStructureVersionId(),
				serviceContext);

		saveFile(ddmStructureVersion.getStructureVersionId(), fileId, ddmFormValues);

		return fileId;
	}

	@Override
	protected void doDeleteByClass(long classPK) throws Exception {
		DDMStorageLink storageLink = _ddmStorageLinkLocalService.getClassStorageLink(classPK);

		FileUtil.delete(getFile(storageLink.getStructureId(), classPK));

		_ddmStorageLinkLocalService.deleteClassStorageLink(classPK);
		
	}

	@Override
	protected void doDeleteByDDMStructure(long ddmStructureId) throws Exception {
		FileUtil.deltree(getStructureFolder(ddmStructureId));

		_ddmStorageLinkLocalService.deleteStructureStorageLinks(ddmStructureId);
		
	}

	@Override
	protected DDMFormValues doGetDDMFormValues(long classPK) throws Exception {
		DDMStorageLink storageLink = _ddmStorageLinkLocalService.getClassStorageLink(classPK);

		DDMStructureVersion structureVersion = _ddmStructureVersionLocalService
				.getStructureVersion(storageLink.getStructureVersionId());

		String serializedDDMFormValues = FileUtil.read(getFile(structureVersion.getStructureVersionId(), classPK));

		return _ddmFormValuesJSONDeserializer.deserialize(structureVersion.getDDMForm(), serializedDDMFormValues);
	}

	@Override
	protected void doUpdate(long classPK, DDMFormValues ddmFormValues, ServiceContext serviceContext) throws Exception {
		validate(ddmFormValues, serviceContext);

		DDMStorageLink storageLink = _ddmStorageLinkLocalService.getClassStorageLink(classPK);

		saveFile(storageLink.getStructureVersionId(), storageLink.getClassPK(), ddmFormValues);
		
	}
	
private File getFile(long structureId, long fileId) {
		
		return new File("Forms", String.valueOf(fileId));
	}

	private File getStructureFolder(long structureId) {
		return new File(String.valueOf(structureId));
	}

	private void saveFile(long structureVersionId, long fileId, DDMFormValues formValues) throws IOException {
		
		

		String serializedDDMFormValues = _ddmFormValuesJSONSerializer.serialize(formValues);

		File formEntryFile = getFile(structureVersionId, fileId);

		System.out.println("#####################Name of the file ###########################" + formEntryFile.getName());

		System.out.println("################Absolute Path of the file ##################### " + formEntryFile.getAbsolutePath());

		FileUtil.write(formEntryFile, serializedDDMFormValues);
	}

	protected void validate(DDMFormValues ddmFormValues, ServiceContext serviceContext) throws Exception {

		boolean validateDDMFormValues = GetterUtil.getBoolean(serviceContext.getAttribute("validateDDMFormValues"),
				true);

		if (!validateDDMFormValues) {
			return;
		}

		_ddmFormValuesValidator.validate(ddmFormValues);
	}

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Reference
	private DDMFormValuesJSONSerializer _ddmFormValuesJSONSerializer;

	@Reference
	private DDMFormValuesValidator _ddmFormValuesValidator;

	@Reference
	private DDMFormValuesJSONDeserializer _ddmFormValuesJSONDeserializer;
	
	
}