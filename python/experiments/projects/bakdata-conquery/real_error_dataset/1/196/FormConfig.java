package com.bakdata.conquery.apiv1.forms;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.bakdata.conquery.io.jackson.InternalOnly;
import com.bakdata.conquery.models.auth.entities.User;
import com.bakdata.conquery.models.execution.Labelable;
import com.bakdata.conquery.models.execution.Shareable;
import com.bakdata.conquery.models.execution.Taggable;
import com.bakdata.conquery.models.identifiable.IdentifiableImpl;
import com.bakdata.conquery.models.identifiable.ids.NamespacedId;
import com.bakdata.conquery.models.identifiable.ids.specific.DatasetId;
import com.bakdata.conquery.models.identifiable.ids.specific.FormConfigId;
import com.bakdata.conquery.models.identifiable.ids.specific.UserId;
import com.bakdata.conquery.models.query.QueryTranslator;
import com.bakdata.conquery.models.worker.Namespaces;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.constraints.NotEmpty;

@Slf4j
@Data
@AllArgsConstructor
public class FormConfig extends IdentifiableImpl<FormConfigId> implements Shareable, Labelable, Taggable{

	@NotEmpty
	private String formType;
	private UUID formId = UUID.randomUUID();
	private String label = formId.toString();
	@NotNull
	private String[] tags = ArrayUtils.EMPTY_STRING_ARRAY;
	private boolean shared = false;
	@NotNull
	private JsonNode values;
	@InternalOnly
	protected UserId owner;

	@Override
	public FormConfigId createId() {
		return new FormConfigId(formType, formId);
	}

	/**
	 * Provides an overview (meta data) of this form configuration without the
	 * actual form field values.
	 */
	public FormConfigOverviewRepresentation overview(Namespaces namespaces, User user) {
		@NonNull
		String ownerName = Optional.ofNullable(namespaces.getMetaStorage().getUser(owner)).map(User::getLabel).orElse(null);

		return FormConfigOverviewRepresentation.builder().id(getId()).formType(formType).label(label).tags(tags).ownerName(ownerName)
			.own(owner.equals(user.getId())).shared(shared)
			// system?
			.build();
	}

	/**
	 * Tries to convert the given raw form to the provided dataset. It does not
	 * check whether the {@link NamespacedId} that are converted in this processes
	 * are actually resolvable. Also, it tries to map the values to a subclass of
	 * {@link Form}, for conversion. If that is not possible the untranslated values
	 * are output.
	 */
	public FormConfigFullRepresentation tryTranslateToDataset(Namespaces namespaces, DatasetId target, ObjectMapper mapper, User user) {
		JsonNode finalRep = values;
		try {
			Form intemediateRep = mapper.readerFor(Form.class).readValue(values.traverse());
			Form translatedRep = QueryTranslator.replaceDataset(namespaces, intemediateRep, target);
			finalRep = mapper.valueToTree(translatedRep);
		}
		catch (IOException e) {
			log.warn("Unable to translate form configuration {} to dataset {}. Sending untranslated version.", getId(), target);
		}

		@NonNull
		String ownerName = Optional.ofNullable(namespaces.getMetaStorage().getUser(owner)).map(User::getLabel).orElse(null);

		return FormConfigFullRepresentation.builder().id(getId()).formType(formType).label(label).tags(tags).ownerName(ownerName)
			.own(owner.equals(user.getId())).shared(shared)
			// system?
			.values(finalRep).build();
	}

	/**
	 * API representation for the overview of all {@link FormConfig}s which does not
	 * include the form fields an their values.
	 */
	@Getter
	@SuperBuilder
	public static class FormConfigOverviewRepresentation {

		private FormConfigId id;
		private String formType;
		private String label;
		private String[] tags;

		private String ownerName;
		private boolean own;
		private boolean shared;
		private boolean system;

	}

	/**
	 * API representation for a single {@link FormConfig} which includes the form
	 * fields an their values.
	 */
	@Getter
	@SuperBuilder
	public static class FormConfigFullRepresentation extends FormConfigOverviewRepresentation {

		private JsonNode values;
	}

}
