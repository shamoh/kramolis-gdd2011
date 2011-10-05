import groovyx.gaelyk.spock.*
import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

class DatastoreServiceSpec extends GaelykUnitSpec {

	def setup() {
		groovlet 'dataStoreGroovlet.groovy'
	}

	def "the datastore is used from within the groovlet"() {
		given: "the initialised groovlet is invoked and data is persisted"
		dataStoreGroovlet.get()

		when: "the datastore is queried for data"
		def query = new Query("person")
		query.addFilter("firstname", Query.FilterOperator.EQUAL, "Marco")
		def preparedQuery = datastore.prepare(query)
		def entities = preparedQuery.asList(withLimit(1))

		then: "the persisted data is found in the datastore"
		def person = entities[0]
		person.firstname == 'Bart'
		person.lastname == 'Simpson'
	}

}
