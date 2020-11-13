package org.github.felipegutierrez.explore.akka.recap

object AdvancedSelfTypes {

  /** using dependency Injection */
  class Component {
    // API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component)

  /** using the Cake Pattern */
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }
  trait ScalaDependentComponent { self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + "this is the cake pattern in action"
  }
  trait ScalaApplication { self: ScalaDependentComponent => }

  /** calling the cake pattern in a layer 1 using small components */
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent
  /** calling the cake pattern in a layer 2 using compose */
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats
  /** calling the cake pattern in a layer 3 using app */
  trait AnalyticsApp extends ScalaApplication with Analytics
}
