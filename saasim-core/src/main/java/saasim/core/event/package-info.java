/**
 * This packages contains classes related to the event-driven nature of this simulation.
 * We opted out to sort events in a discrete <code>long</code> ordered timeline. Ties are handled by priority 
 * of events and, in case of another tie, by dispatch order.
 *
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
package saasim.core.event;