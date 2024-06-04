import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.samples.PetClinicApplication;

public class ModulithApplicationTests {

	ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);

	@Test
	void shouldBeCompliant() {
		modules.forEach(System.out::println);
		modules.verify();
	}

	@Test
	void writeDocumentationSnippets() {
		new Documenter(modules).writeModuleCanvases().writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
	}

}
